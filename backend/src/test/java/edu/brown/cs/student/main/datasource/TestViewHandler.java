package edu.brown.cs.student.main.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.LoadCsvHandler;
import edu.brown.cs.student.main.server.handlers.ViewCsvHandler;
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

public class TestViewHandler {

  /** Taken directly from the livecode as stencil. */
  @BeforeAll
  public static void setupBeforeEverything() {
    // Set the Spark port number. This can only be done once, and has to
    // happen before any route maps are added.
    Spark.port(0); // Select an arbitrary port.
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /** Taken directly from the livecode as stencil. */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on the broadband endpoint.
    Spark.unmap("viewcsv");
    Spark.awaitStop(); // Don't proceed until the server is stopped.
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>>
      adapter; // Adapter for serializing server response -> hashmap.
  private final ViewCsvHandler viewHandler = new ViewCsvHandler();
  private final LoadCsvHandler loadHandler = new LoadCsvHandler();
  /**
   * Runs before EACH testcase. Taken generally (with some modifications) from the livecode as
   * stencil.
   */
  @BeforeEach
  public void setup() {
    Spark.get("viewcsv", viewHandler);
    Spark.get("loadcsv", loadHandler);
    Spark.awaitInitialization(); // Don't continue until the server is listening.

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
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
   * Create invalid queries that errors SHOULD be thrown for.
   *
   * @throws IOException Signifier to the test itself in case the request fails.
   */
  @Test
  public void testInvalidQueries() throws IOException {

    // This is an edge case where we didn't load in the CSV beforehand.
    HttpURLConnection loadConnection1 = tryRequest("viewcsv");
    assertEquals(200, loadConnection1.getResponseCode());
    Map<String, Object> body1 =
        adapter.fromJson(new Buffer().readFrom(loadConnection1.getInputStream()));
    Assertions.assertEquals("error_datasource", body1.get("result"));

    // Input a completely invalid query as another edge case. This one should just throw an IO
    // exception that is caught in the handler subsequently.
    HttpURLConnection loadConnection2 = tryRequest("viewcsvadftadfadsfa");
    Assertions.assertThrows(
        IOException.class,
        () -> adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream())));
  }

  /**
   * These are tests for successful queries to the server. Should have a full, coherent, filepath
   * and the output should read as "success".
   *
   * @throws IOException Thrown as a signifier to the test method.
   */
  @Test
  public void testSuccessfulQueries() throws IOException {

    // Try to re-request the server after having loaded in the CSV.
    HttpURLConnection loadConnection1 = tryRequest("loadcsv?filepath=data/ri_income.csv");
    assertEquals(200, loadConnection1.getResponseCode());

    // Now requery to get the 2D array, deserialized, output.
    HttpURLConnection loadConnection2 = tryRequest("viewcsv");
    assertEquals(200, loadConnection1.getResponseCode());
    Map<String, Object> body1 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    Assertions.assertEquals("success", body1.get("result"));

    // Check if the returned queries are correct manually by inspecting the output.
  }
}
