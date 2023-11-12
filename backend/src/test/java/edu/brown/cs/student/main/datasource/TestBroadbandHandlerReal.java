package edu.brown.cs.student.main.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandDataSource;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.CensusAPIBroadbandSource;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This test class does integration testing for the Broadband handler class, which is the class
 * responsible for taking requests -> sending a request to the census API -> processing the
 * response.
 */
public class TestBroadbandHandlerReal {

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

  /**
   * Runs before EACH testcase. Taken generally (with some modifications) from the livecode as
   * stencil.
   */
  @BeforeEach
  public void setup() {
    // 1. We should reinitialize the fields below for each test method.
    // 2. Because this suite is for integration testing, we should use mocked data to prevent from
    // re-querying the server a bunch of times and getting an error back.
    BroadbandDataSource realSource = new CensusAPIBroadbandSource();
    Spark.get(
        "broadband",
        new BroadbandHandler(
            realSource,
            false)); // Don't cache since that user story was removed. Separate caching tests are in
    // the appropriate class.
    Spark.awaitInitialization(); // Don't continue until the server is listening.

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    Moshi moshi = new Moshi.Builder().build();
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
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");
    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This tests whether we can request our server unsuccessfully with an ILL-FORMED JSON request.
   * We're just replicating the body of the handle method here, and are anticipating an exception to
   * be thrown by the part of the method that would be affected by it.
   *
   * <p>All the below IOExceptions will be caught -> DataSourceException will be thrown ->
   * appropriate message will be shown.
   *
   * @throws IOException If there is some sort of ill-formed field for requests. Just a signifier
   *     here.
   */
  @Test
  public void testIllFormedJSONRequest() throws IOException {

    // Set up and make the ill-formed request with nonexistent states/counties, which should throw
    // an IO exception from Moshi (ill-formed request)
    HttpURLConnection loadConnection1 =
        tryRequest("broadband?state=Calivornia&county=Aladuma County");
    // Get the expected response: a thrown IO exception
    Assertions.assertThrows(
        IOException.class,
        () -> adapter.fromJson(new Buffer().readFrom(loadConnection1.getInputStream())));
    loadConnection1.disconnect();

    // Set up and make another ill-formed request, which doesn't have the required fields needed for
    // queries.
    HttpURLConnection loadConnection2 = tryRequest("broadband?adsf");
    Assertions.assertThrows(
        IOException.class,
        () -> adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream())));
    loadConnection2.disconnect();

    // Another ill-formed request where now nothing is passed in for the queries/
    HttpURLConnection loadConnection3 = tryRequest("broadband?state&country");
    Assertions.assertThrows(
        IOException.class,
        () -> adapter.fromJson(new Buffer().readFrom(loadConnection3.getInputStream())));
    loadConnection3.disconnect();
  }
}
