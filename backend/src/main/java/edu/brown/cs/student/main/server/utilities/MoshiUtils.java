package edu.brown.cs.student.main.server.utilities;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import okio.Buffer;

/** Utility class for working with Moshi. */
public class MoshiUtils {

  /**
   * Fetches data from the specified URL and deserializes it into an object of type T using Moshi.
   *
   * @param <T> The type into which the JSON data should be deserialized.
   * @param requestURL The URL from which the JSON data should be fetched.
   * @param dataType The type information for deserialization.
   * @return An instance of type T representing the deserialized data.
   * @throws IOException If there's an I/O error while fetching or deserializing the data.
   * @throws DatasourceException If there's an issue with the data source or the connection.
   */
  public static <T> T fetchAndDeserialize(URL requestURL, Type dataType)
      throws IOException, DatasourceException {
    HttpURLConnection clientConnection = HttpConnectionUtils.connect(requestURL);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<T> adapter = moshi.adapter(dataType);
    T data = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    clientConnection.disconnect();
    return data;
  }
}
