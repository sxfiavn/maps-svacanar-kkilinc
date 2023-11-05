package server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import server.csv.parse.CreatorFromRow;
import server.csv.parse.parser;
import server.csv.search.SearchMethod;
import spark.Request;
import spark.Response;
import spark.Route;
import server.Loaded;

/**
 * Handler class for the loading csv endpoint.
 *
 * <p>It takes a basic GET request with no Json body, and returns a Json object in reply.
 */
public class LoadHandler implements Route {

  /**
   * Calling our parser method from CSV and updating csvLoad field. Saving the parsedData into a
   * csvLoad attribute.
   *
   * @param request the request to handle, containing the filepath of the csv to search
   * @param response use to modify properties of the response
   * @return response content
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String filePathParam = request.queryParams("filePath");
      Reader fileReader = new FileReader(filePathParam);
      // Create an instance of parser class:
      // We know it will parse a List<String> in this main method for search functionality.
      parser<List<String>> fileParser = new parser<>();
      // Create an instance of SearchMethod:
      // SearchMethod is the specific method used for search functionality to parse the data.
      CreatorFromRow<List<String>> searchMethod = new SearchMethod();
      // Call and return parser method using the specific search method and file reader:
      List<List<String>> fileData = fileParser.Parser(fileReader, searchMethod);
      // Update the server's current csvload
      Server.setLoad(fileData);
      return new LoadSuccessResponse().serialize(); // don't need to return parameters given
    } catch (FileNotFoundException e) {
      return new LoadFailureResponse("error_bad_request", e.getMessage()).serialize();
    }
  }
  /** Response object to send, containing a success message */
  public record LoadSuccessResponse(String responseType) {
    public LoadSuccessResponse() {
      this("success");
    }

    /**
     * @return this success response, serialized as Json
     */
    String serialize() {
      try {
        // Just like in SoupAPIUtilities.
        //   (How could we rearrange these similar methods better?)
        Moshi moshi = new Moshi.Builder()
                .add(
                        // Expect something that's an Ingredient...
                        PolymorphicJsonAdapterFactory.of(Loaded.class, "type")
//                              .withSubtype(Carrots.class, "carrot")
                )
                .build();
        JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Response object to send if parsing was unsuccessful due to an incorrect/invalid filepath given,
   * containing an error type and description
   */
  public record LoadFailureResponse(String responseType, String errorType, String errorDesc) {
    public LoadFailureResponse(String errorType, String errorDesc) {
      this("failure", errorType, errorDesc);
    }

    /**
     * @return this failure response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadFailureResponse.class).toJson(this);
    }
  }
}
