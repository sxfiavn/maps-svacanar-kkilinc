package edu.brown.cs.student.main.server.utilities;

import edu.brown.cs.student.main.server.DatasourceException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/** Utility class that is related to HTTP Connections. */
public class HttpConnectionUtils {

  /**
   * Establishes a connection to the given URL and returns an instance of {@link HttpURLConnection}.
   *
   * @param requestURL The URL to which a connection is to be established.
   * @return An instance of HttpURLConnection representing the connection to the given URL.
   * @throws DatasourceException If the result of the connection isn't an instance of
   *     HttpURLConnection or if the API connection does not return a success status.
   * @throws IOException If an I/O exception occurs while opening the connection.
   */
  public static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection =
        (HttpURLConnection)
            urlConnection; // Tim's short answer : couldn't figure out how to do without type
    // casting
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200 && clientConnection.getResponseCode() != 204) {
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }
}
