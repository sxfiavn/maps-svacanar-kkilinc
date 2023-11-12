package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.datasource.redline_caching.CachedJsonDataSource;
import edu.brown.cs.student.main.server.datasource.redline_no_caching.GeoJsonDataSource;
import edu.brown.cs.student.main.server.datasource.redline_no_caching.JsonData;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for view red line data. The handler processes the request and returns a JSON-formatted
 * response containing the success/failure of the request.
 */
public class FilterHandler implements Route {

  private final GeoJsonDataSource state;

  public FilterHandler(GeoJsonDataSource state, Boolean yesCache) {
    this.state = yesCache ? new CachedJsonDataSource(state, 10000, 10) : state;
  }

  /**
   * Handles the incoming request to retrieve the red line data.
   *
   * @param request The incoming request.
   * @param response The outgoing response.
   * @return JSON-formatted string containing the retrieved data/an error message.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Create a map to store the response data.
    Map<String, Object> filterResponseMap = new HashMap<>();

    // Moshi object for JSON parsing, and a JSON adapter for Map<String, Object>.
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    // Get the query parameters from the request.
    String lowerX = request.queryParams("lowerX");
    String upperX = request.queryParams("upperX");
    String lowerY = request.queryParams("lowerY");
    String upperY = request.queryParams("upperY");

    try {
      int lx = Integer.parseInt(lowerX);
      int ux = Integer.parseInt(upperX);
      int ly = Integer.parseInt(lowerY);
      int uy = Integer.parseInt(upperY);

      JsonData featuresInBox = this.state.searchFeaturesInBoundingBox(lx, ly, ux, uy);

      // Put all the data into the response map.
      filterResponseMap.put("result", "success");
      filterResponseMap.put("lowerX", lowerX);
      filterResponseMap.put("lowerY", lowerY);
      filterResponseMap.put("upperX", upperX);
      filterResponseMap.put("upperY", upperY);
      filterResponseMap.put("featuresInBox", featuresInBox.data());
      filterResponseMap.put("retrieval_time", featuresInBox.retrievalTime());
      filterResponseMap.put("geojson", DataStorage.getCurrentJsonData());

      // Return the response map.
      return adapter.toJson(filterResponseMap);

    } catch (NumberFormatException e) {
      System.err.println("Error: Not a valid integer. Bounding box must be integers.");
    } catch (Exception e) {
      filterResponseMap.put("result", "error_datasource");
      filterResponseMap.put(
          "details", "There was an error with the bounding box, please try again.");
      return adapter.toJson(filterResponseMap);
    }

    return null;
  }
}
