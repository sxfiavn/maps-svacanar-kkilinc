// package edu.brown.cs32.examples.moshiExample.server;

// import com.squareup.moshi.JsonAdapter;
// import com.squareup.moshi.Moshi;
// import com.squareup.moshi.Types;
package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import spark.Request;
import spark.Response;
import spark.Route;

/** */
public class ViewHandler implements Route {

  /**
   * Grabbing the parsed data from the most recently loaded CSV.
   *
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return response content - a 2D JSON array containing the CSV's parsed data
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    // Grab the current csvLoad from the server
    System.out.println("test: " + Server.getLoad() + " " + Server.getLoad() == null
        || Server.getLoad().equals(new ArrayList<>())
        || Server.getLoad().isEmpty());

    if (Server.getLoad() == null
        || Server.getLoad().equals(new ArrayList<>())
        || Server.getLoad().isEmpty()){
      // ensure loadHandler was called before this to parse the csv
      return new ViewFailureResponse(
              "error_bad_request",
          "No non-empty CSV data has been successfully loaded yet.").serialize();
    } else {
      // don't need to return parameters given
      return new ViewSuccessResponse(Server.getLoad()).serialize();
    }
  }

  /** Response object to send, containing a success message and the csv's parsedData */
  public record ViewSuccessResponse(String responseType, List<List<String>> data) {
    public ViewSuccessResponse(List<List<String>> data) {
      this("success", data);
    }
    /**
     * @return this success response, serialized as Json
     */
    Object serialize() {
      try {
        Moshi moshi = new Moshi.Builder()
                .add(
                        PolymorphicJsonAdapterFactory.of(Loaded.class, "type")
//                              .withSubtype(Carrots.class, "carrot")
                )
                .build();
        JsonAdapter<ViewSuccessResponse> adapter = moshi.adapter(ViewSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        return "error in view";
//        return new ViewFailureResponse( "error_bad_json", e.getMessage());

//        throw e;
      }
    }
  }


  /**
   * Response object to send if loadHandler was unsuccessful at parsing, containing an error type
   * and description
   */
  public record ViewFailureResponse(String responseType, String errorType, String errorDesc) {
    public ViewFailureResponse(String errorType, String errorDesc) {
      this("failure", errorType, errorDesc);
    }

    /**
     * @return this failure response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ViewFailureResponse.class).toJson(this);
    }
  }
}
