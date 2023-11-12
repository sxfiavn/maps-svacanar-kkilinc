package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csv.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.CreateArrayList;
import edu.brown.cs.student.main.csv.parser.CsvParser;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * A handler for load csv data. The handler processes the request and returns a JSON-formatted
 * response containing the success/failure of the request.
 */
public class LoadCsvHandler implements Route {

  /**
   * Handles incoming requests for loading the CSV.
   *
   * @param request Incoming request from the user. Used to get query parameters.
   * @param response Not used, but needed field when overriding the handle method.
   * @return A deserialized object generated from a hashmap storing success and error codes, as well
   *     as any other objects.
   */
  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    String filepath = request.queryParams("filepath");

    if (filepath == null || filepath.trim().isEmpty()) {
      responseMap.put("result", "error_bad_request");
      responseMap.put("filepath", filepath);
      return adapter.toJson(responseMap);
    }

    try {

      // Initialize necessary objects to pass into the CsvParser.
      FileReader fileReader = new FileReader(filepath);
      CreateArrayList creatorFromRow = new CreateArrayList();
      CsvParser<ArrayList<String>> csvParser = new CsvParser<>(fileReader, creatorFromRow);

      // Parse the csv data and store it for access across handlers.
      DataStorage.setCurrentCSVData(csvParser.parse());
      responseMap.put("result", "success");
      responseMap.put("filepath", filepath);

      DataStorage.setFilepath(filepath);

      return adapter.toJson(responseMap);

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());

      System.out.println("This is the source of the error: " + Arrays.toString(e.getStackTrace()));
      responseMap.put("result", "error_datasource");
      responseMap.put("details of error: ", e.getMessage());

    } catch (FactoryFailureException | IOException e) {
      System.out.println(e.getMessage());

      System.out.println("This is the source of the error: " + Arrays.toString(e.getStackTrace()));
      responseMap.put("result", "error_bad_json");
      responseMap.put("details of error: ", e.getMessage());

    } catch (Exception e) {
      System.out.println(e.getMessage());

      System.out.println("This is the source of the error: " + Arrays.toString(e.getStackTrace()));
      responseMap.put("result", "error_unknown");
      responseMap.put("details of error: ", e.getMessage());
    }
    return adapter.toJson(responseMap);
  }
}
