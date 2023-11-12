package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.DatasourceException;
import edu.brown.cs.student.main.server.datasource.broadband_caching.CachedBroadbandDataSource;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandData;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandDataSource;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for broadband data retrieval based on state and county name queries. The handler
 * processes the request, queries the broadband data source, and returns a JSON-formatted response
 * containing the retrieved broadband data.
 */
public class BroadbandHandler implements Route {
  private final BroadbandDataSource state;

  /**
   * Constructs a new BroadbandHandler with the specified CensusAPIBroadbandSource source. Since the
   * caching behavior is not the default, and should be switched on or off completely according to
   * developer preferences, the constructor allows switching between regular caching/not.
   *
   * @param state CensusAPIBroadbandSource to be used and cached.
   */
  public BroadbandHandler(BroadbandDataSource state, Boolean yesCache) {
    if (yesCache) {
      this.state = new CachedBroadbandDataSource(state, 1000, 10);
    } else {
      this.state = state;
    }
  }

  /**
   * Handles the incoming request to retrieve broadband data.
   *
   * @param request The incoming request.
   * @param response The outgoing response.
   * @return JSON-formatted string containing the retrieved broadband data/an error message.
   */
  @Override
  public Object handle(Request request, Response response) throws DatasourceException {

    String stateName = request.queryParams("state");
    String countyName = request.queryParams("county");

    // Throw error if the required fields are not provided/are null.
    if (countyName == null
        || stateName == null
        || countyName.trim().isEmpty()
        || stateName.trim().isEmpty()) {
      throw new DatasourceException("Bad request");
    }

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Map<String, Object> responseMap = new HashMap<>();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    JsonAdapter<BroadbandData> broadbandDataJsonAdapter = moshi.adapter(BroadbandData.class);

    try {
      BroadbandData data = this.state.getBroadbandData(stateName, countyName);

      responseMap.put("result", "success");
      // For some weird reason toJson doesn't work but toJsonValue does! Use this to deserialize to
      // Json.
      responseMap.put("data", broadbandDataJsonAdapter.toJsonValue(data));
      responseMap.put("state name query", stateName);
      responseMap.put("county name query", countyName);
      return adapter.toJson(responseMap);
    } catch (DatasourceException e) {

      // Get the corresponding messages from the thrown messages and place them into the response
      // map.
      if (e.getMessage().equals("Malformed JSON")
          || e.getMessage().equals("Well-formed JSON, but unexpected format")) {
        System.out.println(
            "This is the source of the error: " + Arrays.toString(e.getStackTrace()));
        responseMap.put("result", "error_bad_json");
        responseMap.put("details of error", e.getMessage());
      } else if (e.getMessage().equals("Bad request")) {
        System.out.println(
            "This is the source of the error: " + Arrays.toString(e.getStackTrace()));
        responseMap.put("result", "error_bad_request");
        responseMap.put("details of error", e.getMessage());
      } else if (e.getMessage().equals("No data returned from API")) {
        System.out.println(
            "This is the source of the error: " + Arrays.toString(e.getStackTrace()));
        responseMap.put("result", "error_datasource");
        responseMap.put("details of error", e.getMessage());
      } else {
        responseMap.put("result", "invalid_input");
      }
      // Put additional information into the map regardless.
      responseMap.put("state name query", stateName);
      responseMap.put("county name query", countyName);
      return adapter.toJson(responseMap);
    } catch (Exception e) {
      // checks

    }
    responseMap.put("result", "error_bad_request");
    responseMap.put("state name query", stateName);
    responseMap.put("county name query", countyName);
    return adapter.toJson(responseMap);
  }
}
