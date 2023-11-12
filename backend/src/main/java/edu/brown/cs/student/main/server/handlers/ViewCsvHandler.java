package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for view csv data. The handler processes the request and returns a JSON-formatted
 * response containing the success/failure of the request.
 */
public class ViewCsvHandler implements Route {

  /**
   * Handles the incoming request to retrieve the csv data.
   *
   * @param request The incoming request.
   * @param response The outgoing response.
   * @return JSON-formatted string containing the retrieved data/an error message.
   */
  @Override
  public Object handle(Request request, Response response) {

    // Set up Moshi and map object
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {

      responseMap.put("result", "success");
      responseMap.put("data", DataStorage.getCurrentCSVData());
      return adapter.toJson(responseMap);

      // No other exceptions to handle besides those that wouldn't already have been caught by
      // loadCSV.
      // Any other errors would be due to Moshi, but those would be caught by this generic block.

      // This exception is meant to be thrown whenever the datasource isn't loaded (i.e. the csv
      // file).
    } catch (Exception e) {
      responseMap.put("result", "error_datasource");
      responseMap.put("details", "CSV was not loaded! Please load first and try again.");
      return adapter.toJson(responseMap);
    }
  }
}
