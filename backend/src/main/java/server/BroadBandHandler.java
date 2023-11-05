// package edu.brown.cs32.examples.moshiExample.server;

// import com.squareup.moshi.JsonAdapter;
// import com.squareup.moshi.Moshi;
// import com.squareup.moshi.Types;
package server;

import com.google.common.cache.Cache;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import server.caching.ApiCall;

/** */
public class BroadBandHandler implements Route {

  HashMap<String, String> StateMap;
  HashMap<String, HashMap<String, String>> CountyMap;

  Cache<String, String> Cache;

  public BroadBandHandler(
      HashMap<String, String> state,
      HashMap<String, HashMap<String, String>> county,
      Cache<String, String> cache){
    this.StateMap = state;
    this.CountyMap = county;
    this.Cache = cache; // Google's guava cache library
  }


  /**
   * Looks up the state code, county code through ACS API to retrieve the percentage of broadband
   * access for a given county and state in the US
   *
   * @return JSON object with a dictionary of all input parameters, the date/time ran, the broadband
   *     value given input parameters
   * @param request the request to handle, containing the user's input state and county to find the
   *     broadband value of
   * @param response use to modify properties of the response
   * @return response content - a 2D JSON array containing the broadband percentage
   */
  @Override
  public Object handle(Request request, Response response) {

    String state = request.queryParams("state");
    String county = request.queryParams("county");

    if (state.isEmpty() || state.equals("*")){
      return new BroadBandFailureResponse("error_bad_request",
          "No State Entered").serialize();
    }

    if (county.isEmpty() || county.equals("*")){
      return new BroadBandFailureResponse("error_bad_request",
          "No County Entered").serialize();
    }

    String county_state = county + ", " + state;

    String percentage;

    // Get the state code and the county code from the StateMap and CountyMap
    if (StateMap.containsKey(state)) {
      String state_code = StateMap.get(state);

      if (CountyMap.containsKey(state_code) && CountyMap.get(state_code).containsKey(county)) {

        String county_code = CountyMap.get(state_code).get(county);

        // Will return null if key is not in the cache, or a value if it is.
        //percentage = Cache.getIfPresent(county_state);

        // If key didn't exist in cache
        //if (percentage == null){
          // call method in ApiCall to get percentage using URL, state and county
          String URL = "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,"
              + "S2802_C03_022E&for=county:" + county_code + "&in=state:" + state_code;

          ApiCall call = new ApiCall(state_code, county_code, URL);
          percentage = call.GetPercentage();

          if (percentage == null){
            return new BroadBandFailureResponse("error_datasource",
                "Error Loading ACS API").serialize();
          }

          // Code 204?
          // In the case of state + county combination having no corresponding data in API
          if (percentage.equals("EMPTY ROW")) {
            return new BroadBandFailureResponse("no_data",
                "No Data Matched your request").serialize();
          }

          // Add it to the cache with "County, State" as key & percentage as value
         // Cache.put(county_state, percentage);
       // }

      } else {
        // Case where county is not found
        return new BroadBandFailureResponse("error_bad_request",
            "County Input Not Found").serialize();
      }
    } else {
      // Case where state is not found
      return new BroadBandFailureResponse("error_bad_request",
          "State Input Not Found").serialize();
    }


    Map<String, String> paramsDict = new HashMap<>();
    paramsDict.put("county", county);
    paramsDict.put("state", state);

    LocalDateTime currTime = java.time.LocalDateTime.now();

    return new BroadBandSuccessResponse(paramsDict, percentage, currTime).serialize();
  }

  /**
   * Class to adapt time into JSON format
   */
  public static class LocalDateTimeAdapter {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @ToJson
    String toJson(LocalDateTime localDateTime) {
      return formatter.format(localDateTime);
    }

    @FromJson
    LocalDateTime fromJson(String json) {
      return LocalDateTime.parse(json, formatter);
    }
  }


  /**
   * Response object to send, containing a success message, a dictionary of the user's input
   * parameters for search, and a list of rows in the csv that have matches with the user's search
   * term.
   *
   * @param responseType - success message
   * @param paramsDict - dictionary with state and county names provided by the user
   * @param currTime - what time this handler was called
   * // @param broadband - broadband value found for the given state and county
   */
  public record BroadBandSuccessResponse(
      String responseType,
      Map<String, String> paramsDict,
      String API_result,
      LocalDateTime currTime) {

    public BroadBandSuccessResponse(
        Map<String, String> paramsDict,
        String API_result,
        LocalDateTime currTime) {
      this("success", paramsDict, API_result, currTime);
    }

    /**
     * @return this success response, serialized as Json
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder()
            .add(new LocalDateTimeAdapter())
            .build();
        JsonAdapter<BroadBandSuccessResponse> adapter = moshi.adapter(BroadBandSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        return "error in broadband";
//        throw e;
      }
    }
  }

  /**
   * Response object to send if the API calls were unsuccessful due to invalid inputs, no broadband
   * value found etc...
   */
  public record BroadBandFailureResponse(String responseType, String errorType, String errorDesc) {
    public BroadBandFailureResponse(String errorType, String errorDesc) {
      this("failure", errorType, errorDesc);
    }

    /**
     * @return this failure response, serialized as Json
     */
    private String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(BroadBandFailureResponse.class).toJson(this);
    }
  }
}
