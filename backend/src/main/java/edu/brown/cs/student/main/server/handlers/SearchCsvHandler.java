package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.searcher.Search;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for search csv data. The handler processes the request and returns a JSON-formatted
 * response containing the success/failure of the request.
 */
public class SearchCsvHandler implements Route {

  /**
   * Handles incoming requests for searching the CSV.
   *
   * @param request Incoming request from the user. Used to get query parameters.
   * @param response Not used, but needed field when overriding the handle method.
   * @return A deserialized object generated from a hashmap storing success and error codes, as well
   *     as any other objects.
   */
  @Override
  public Object handle(Request request, Response response) {
    // Setup Moshi
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class); // create generic
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    Boolean hasHeader = false;

    // Get query parameters
    String target = request.queryParams("target").toLowerCase();
    String identifier = request.queryParams("identifier").toLowerCase();
    String headerParam = request.queryParams("header");

    if (headerParam != null) {
      hasHeader = Boolean.parseBoolean(headerParam.toLowerCase());
    } else {
      hasHeader = Boolean.FALSE;
    }

    // Throw error if the required fields are not provided/are null.
    if (target.trim().isEmpty() || identifier.trim().isEmpty()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("target query", target);
      responseMap.put("identifier query", identifier);
      responseMap.put("hasHeader query", hasHeader);
      System.err.println("Bad request! Please rewrite your querie(s) and try again.");
      return adapter.toJson(responseMap);
    }

    try {
      FileReader fileReader = new FileReader(DataStorage.getFilepath());
      Search searcher = new Search(fileReader, target, identifier, hasHeader);
      responseMap.put("result", "success");
      responseMap.put("data", searcher.beginSearch(DataStorage.getCurrentCSVData()));
      responseMap.put("target query", target);
      responseMap.put("identifier query", identifier);
      responseMap.put("hasHeader query", hasHeader);
      return adapter.toJson(responseMap);

    } catch (IOException e) {
      System.out.println(e.getMessage());

      System.out.println("This is the source of the error: " + Arrays.toString(e.getStackTrace()));
      responseMap.put("result", "error_datasource");
      responseMap.put("details of error: ", e.getMessage());
      responseMap.put("target query", target);
      responseMap.put("identifier query", identifier);
      responseMap.put("hasHeader query", hasHeader);

    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("This is the source of the error: " + Arrays.toString(e.getStackTrace()));
      responseMap.put("result", "error_no_file_loaded");
      responseMap.put("target query", target);
      responseMap.put("identifier query", identifier);
      responseMap.put("hasHeader query", hasHeader);
      responseMap.put("details of error: ", e.getMessage());
    }
    return adapter.toJson(responseMap);
  }
}
