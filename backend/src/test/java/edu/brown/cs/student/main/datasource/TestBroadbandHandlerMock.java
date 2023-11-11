package edu.brown.cs.student.main.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.datasource.mocks.MockedCensusAPIBroadbandSource;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandData;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandDataSource;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This test class does integration testing for the Broadband handler class, which is the class
 * responsible for taking requests -> sending a request to the census API -> processing the
 * response.
 */
public class TestBroadbandHandlerMock {

  /** Setup that must run before EVERY test case. Declared static for that reason. */
  @BeforeAll
  public static void setupBeforeEverything() {

    // Set the Spark port number. This can only be done once, and has to
    // happen before any route maps are added.
    Spark.port(0); // Select an arbitrary port.
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  // These fields help Moshi serialize objects. Inspired directly from the livecode
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>>
      adapter; // Adapter for serializing server response -> hashmap.
  private JsonAdapter<BroadbandData>
      broadbandDataAdapter; // Adapter for serializing hashmap object -> BroadbandData record.

  /**
   * Runs before EACH testcase. Taken generally (with some modifications) from the livecode as
   * stencil.
   */
  @BeforeEach
  public void setup() {

    // 1. We should reinitialize the fields below for each test method.
    // 2. Because this suite is for integration testing, we should use mocked data to prevent from
    // re-querying the server a bunch of times and getting an error back.
    BroadbandDataSource mockedSource =
        new MockedCensusAPIBroadbandSource(
            new BroadbandData(
                "Alameda County, California", "89.9", "06", "001", null, "2023-09-28 11:01:47"));
    Spark.get(
        "broadband",
        new BroadbandHandler(
            mockedSource,
            false)); // Don't cache since that user story was removed. Separate caching tests are in
    // the appropriate class.
    Spark.awaitInitialization(); // Don't continue until the server is listening

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    Moshi moshi = new Moshi.Builder().build();
    this.broadbandDataAdapter = moshi.adapter(BroadbandData.class);
    adapter = moshi.adapter(mapStringObject);
  }

  /** Runs after EACH testcase runs. Taken directly from the livecode as stencil. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on the broadband endpoint.
    Spark.unmap("broadband");
    Spark.awaitStop(); // Don't proceed until the server is stopped.
  }

  /**
   * Helper to start a connection to a specific API endpoint/params. The "throws IOException" clause
   * doesn't signify anything -- JUnit will just error out if the exception wasn't declared as a
   * parameter. Taken directly from livecode with some modifications as stencil.
   *
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {

    // Configure the connection (but don't actually send a request yet)
    // ** We structure this a bit differently from the livecode because we know what endpoint we're
    // listening at. However we want to test ILL FORMED json requests so keep generic JSON request.
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    System.out.println("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This tests whether we can request our server successfully with a WELL-FORMED JSON request, with
   * all fields provided or not. Realistically this just shows that we can manufacture a correct
   * connection -- unit testing will do the job of showing that some queries are invalid/valid and
   * that they are processed correctly.
   *
   * @throws IOException Just a signifier for the test method here.
   */
  @Test
  public void testBroadbandSuccess() throws IOException {

    // Set up and make the request.
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=California&county=Alameda%20County");

    // Get an OK response that the connection went through.
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: a success
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("success", body.get("result"));

    // Mocked data: correct name, state, county, and date/time data. We know what it is, because we
    // mocked!
    assertEquals(
        broadbandDataAdapter.toJsonValue(
            new BroadbandData(
                "Alameda County, California", "89.9", "06", "001", null, "2023-09-28 11:01:47")),
        body.get("data"));
    loadConnection.disconnect();
  }

  /**
   * Taken directly from the livecode. Shows error body in the terminal if applicable error is
   * thrown.
   *
   * @param body the output from the Moshi adapter deserializing -> Map.
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
