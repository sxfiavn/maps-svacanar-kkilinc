package server;

import java.util.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import server.csv.search.ColumnSearchOption;
import server.csv.search.InvalidSearchException;
import server.csv.search.Searcher;
import spark.Request;
import spark.Response;
import spark.Route;

/** */
public class SearchHandler implements Route {
  Map<String, Object> paramsDict = new HashMap<>(); // TODO: put in constructor instead?

  /**
   * Searches the most recently loaded CSV for rows matching the user's input search term.
   *
   * @return JSON object with all rows that contain a match to the given search term and a
   *     dictionary of all inputs
   * @param request the request to handle, containing all necessary inputs for Searcher's search
   *     method
   * @param response use to modify properties of the response
   * @return response content - a 2D JSON array containing the csv's parsedData
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String searchTerm = request.queryParams("searchTerm");
      String allColumns = request.queryParams("allColumns");
      Boolean allColumnsBool = Boolean.parseBoolean(allColumns); // if this input isn't given, assuming it as false
      String hasHeaders = request.queryParams("hasHeaders");
      Boolean hasHeadersBool = Boolean.parseBoolean(hasHeaders); // if this input isn't given, assuming it as false
      String inputColumn = request.queryParams("inputColumn");
      ColumnSearchOption indexOrHeader;
      try {
        indexOrHeader =
                ColumnSearchOption.valueOf(request.queryParams("indexOrHeader").toUpperCase());
      } catch (IllegalArgumentException e) {
        return new SearchFailureResponse(
                paramsDict,
                "error_bad_request",
                "IndexOrHeader input must be either 'index' or 'header'");
      }
      paramsDict.put("searchTerm", searchTerm);
      paramsDict.put("allColumns", allColumnsBool);
      paramsDict.put("hasHeaders", hasHeadersBool);
      paramsDict.put("inputColumn", inputColumn);
      paramsDict.put("indexOrHeader", indexOrHeader);
      if (searchTerm == "") {

        return new SearchFailureResponse(paramsDict, "error_bad_json",
            "No search term inputted.");
      }
      if (Server.getLoad() == null) {
        return new SearchFailureResponse(paramsDict, "error_bad_json",
            "No CSV data has been successfully loaded yet.");

      }
      Searcher searchObject = new Searcher();
      searchObject.search(
              Server.getLoad(), searchTerm, allColumnsBool, hasHeadersBool, inputColumn, indexOrHeader);
      //            HashMap<Integer, List<String>> hashMap_output = searchObject.rows_to_content;
      List<List<String>> rowContent = searchObject.row_content;
      return new SearchSuccessResponse(paramsDict, rowContent).serialize();
    } catch (InvalidSearchException e){ // thrown if the parameters sent by the user were invalid
      return new SearchFailureResponse(paramsDict, "error_bad_request",
              "Parameters sent by the user were invalid. " + e.getMessage()).serialize();
    } catch (RuntimeException e) { // thrown if the searching process errors
      return new SearchFailureResponse(paramsDict, "error_bad_json",
              "Error during search process. " + e.getMessage()).serialize();
    }
  }

  /**
   * Response object to send, containing a success message, a dictionary of the user's input
   * parameters for search, and a list of rows in the csv that have matches with the user's search
   * term.
   */
  public record SearchSuccessResponse(
      String responseType, Map<String, Object> params, List<List<String>> data) {
    public SearchSuccessResponse(Map<String, Object> params, List<List<String>> data) {
      this("success", params, data);
    }
    /**
     * @return this success response, serialized as Json
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder()
                .add(
                        PolymorphicJsonAdapterFactory.of(Loaded.class, "type")
//                              .withSubtype(Carrots.class, "carrot")
                )
                .build();
        JsonAdapter<SearchSuccessResponse> adapter = moshi.adapter(SearchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        System.out.println("error caught: " + e.getMessage());
        return "error in search handler";
//        return new SearchFailureResponse(new HashMap<>(), "error_bad_json", e.getMessage());

//        throw e;
      }
    }
  }

  /**
   * Response object to send if searching was unsuccessful due to invalid user inputs, IOException,
   * FileNotFoundException, or FactoryFailureException
   */
  public record SearchFailureResponse(
      String responseType,
      Map<String, Object> params,
      List<List<String>> rowsResult,
      String errorType,
      String errorDesc) {
    public SearchFailureResponse(
        Map<String, Object> params, String errorType, String errorDesc) {
      this("failure", params, new ArrayList<>(), errorType, errorDesc);
    }
    /**
     * @return this failure response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchFailureResponse.class).toJson(this);
    }
  }
}
