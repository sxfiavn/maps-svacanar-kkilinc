package serverTests;
import com.google.common.cache.Cache;
import com.squareup.moshi.Moshi;
import java.time.LocalDateTime;
import java.util.HashMap;
import okio.Buffer;

import server.BroadBandHandler;
import server.BroadBandHandler.BroadBandFailureResponse;
import server.BroadBandHandler.BroadBandSuccessResponse;
import server.LoadHandler;
import server.SearchHandler;
import server.ViewHandler;
import server.caching.ApiCallCache;
import server.hashMapManager.StringToCode;

import server.csv.parse.CreatorFromRow;
import server.csv.parse.FactoryFailureException;
import server.csv.parse.parser;
import server.csv.search.InvalidSearchException;
import server.csv.search.SearchMethod;
import server.csv.search.Searcher;
import spark.Spark;

import static server.csv.search.ColumnSearchOption.HEADER;
import static spark.Spark.after;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import java.util.logging.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestAPIHandlers{
    @BeforeAll
        public static void setup_before_everything() {
            // run a server on any port
            Spark.port(0);
//            // ?
//            Logger.getLogger("").setLevel(Level.WARNING);
        }
//
        /**
         * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never need to replace
         * the reference itself. We clear this state out after every test runs.
         */
        @BeforeEach
        public void setup() {
            // Preprocessing Necessary Data For BroadBandHandler
            // HashMaps: K = State/County Name, V = State/County Code.
            StringToCode string_code = new StringToCode();
            HashMap<String, String> StateCodeMap = string_code.GetStateMap();
            HashMap<String, HashMap<String, String>> CountyCodeMap = string_code.GetCountyMap();
            Cache<String, String> cache = new ApiCallCache().getCache();

            // In fact, restart the entire Spark server for every test!
            Spark.get("load", new LoadHandler());
            Spark.get("view", new ViewHandler());
            Spark.get("search", new SearchHandler());
            Spark.get("broadband", new BroadBandHandler(StateCodeMap, CountyCodeMap, cache));

            Spark.init();
            Spark.awaitInitialization(); // don't continue until the server is listening
        }
//
        @AfterEach
        public void teardown() {
            // Gracefully stop Spark listening on both endpoints
            Spark.unmap("/load");
            Spark.unmap("/view");
            Spark.unmap("/search");
            Spark.unmap("/broadband");
            Spark.awaitStop(); // don't proceed until the server is stopped
        }
//
        /**
         * Helper to start a connection to a specific API endpoint/params
         * @param apiCall the call string, including endpoint
         * @return the connection for the given URL, just after connecting
         * @throws IOException if the connection fails for some reason
         */
        static private HttpURLConnection tryRequest(String apiCall) throws IOException {
            // Configure the connection (but don't actually send the request yet)
            URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
            HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

            // The default method is "GET", which is what we're using here.
            // If we were using "POST", we'd need to say so.
            //clientConnection.setRequestMethod("GET");

            clientConnection.connect();
            return clientConnection;
        }

    /**
     * Uses moshi to create a LoadSuccessResponse object to test for loadHandler on normally formatted CSV with headers
     * @throws IOException
     */
        @Test
        public void testAPILoad() throws IOException {

            HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/"
                + "Desktop/CS0320/Server/server-nhaseley-sxfiavn/src/main/data/census/dol_ri_"
                + "earnings_disparity.csv");
            // Get an OK response (the *connection* worked, the *API* provides an error response)
            assertEquals(200, clientConnection.getResponseCode());

            Moshi moshi = new Moshi.Builder().build();
            LoadHandler.LoadSuccessResponse response =
                    moshi.adapter(LoadHandler.LoadSuccessResponse.class).fromJson(

                        new Buffer().readFrom(clientConnection.getInputStream()));

            assertEquals(new LoadHandler.LoadSuccessResponse("success"),response);
            clientConnection.disconnect();
        }
    /**
     * Uses moshi to create a LoadSuccessResponse object to test for loadHandler on an empty CSV
     * @throws IOException
     */
    @Test
    public void testAPILoadEmptyCSV() throws IOException {

        HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/"
            + "Desktop/CS0320/Server/server-nhaseley-sxfiavn/src/main/data/MyCSVs/empty.csv");

        Moshi moshi = new Moshi.Builder().build();
        LoadHandler.LoadSuccessResponse response =
                moshi.adapter(LoadHandler.LoadSuccessResponse.class).fromJson(new Buffer().readFrom(
                    clientConnection.getInputStream()));

        assertEquals(new LoadHandler.LoadSuccessResponse("success"), response);
        clientConnection.disconnect();
    }

    /**
     * Uses moshi to create a LoadSuccessResponse object to test for loadHandler on CSV with an invalid filePath
     * @throws IOException
     */
    @Test
    public void testAPILoadInvalidPath() throws IOException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=invalid.csv");

        Moshi moshi = new Moshi.Builder().build();
        LoadHandler.LoadFailureResponse response =

                moshi.adapter(LoadHandler.LoadFailureResponse.class).fromJson(new Buffer().readFrom
                    (clientConnection.getInputStream()));

        assertEquals(new LoadHandler.LoadFailureResponse("failure", "error_b"
            + "ad_request", "invalid.csv (No such file or directory)"),response);
        clientConnection.disconnect();
    }

    /**
     * Uses moshi to create a LoadSuccessResponse object to test for loadHandler on CSV with no input filePath
     * @throws IOException
     */
    @Test
    public void testAPILoadNullPath() throws IOException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=");

        Moshi moshi = new Moshi.Builder().build();
        LoadHandler.LoadFailureResponse response =
                moshi.adapter(LoadHandler.LoadFailureResponse.class).fromJson(new Buffer().readFrom
                    (clientConnection.getInputStream()));

        assertEquals(new LoadHandler.LoadFailureResponse("failure", "error_ba"
            + "d_request", " (No such file or directory)"), response);
        clientConnection.disconnect();
    }

    /**
     * Uses moshi to create a ViewSuccessResponse object to test for loadHandler on normally formatted CSV with headers
     * @throws IOException
     */
    @Test
    public void testAPIViewCSV() throws IOException, FactoryFailureException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/Des"
            + "ktop/CS0320/Server/server-nhaseley-sxfiavn/src/main/data/census/dol_ri_earnings_"
            + "disparity.csv");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("view");

        Moshi moshi = new Moshi.Builder().build();
        ViewHandler.ViewSuccessResponse response =
                moshi.adapter(ViewHandler.ViewSuccessResponse.class).fromJson(new Buffer().readFrom
                    (clientConnection2.getInputStream()));

        Reader fileReader = new FileReader("/Users/nylevenya/Desktop/CS0320/Server/server"
            + "-nhaseley-sxfiavn/src/main/data/census/dol_ri_earnings_disparity.csv");

        parser<List<String>> fileParser = new parser<>();
        CreatorFromRow<List<String>> searchMethod = new SearchMethod();
        List<List<String>> parsedData = fileParser.Parser(fileReader, searchMethod);
        assertEquals(new ViewHandler.ViewSuccessResponse("success", parsedData), response);
        clientConnection2.disconnect();
    }

    // TODO: EXPECTED BEHAVIOR SUCCESS OR FAIL?
    @Test
    public void testAPIViewEmptyCSV() throws IOException, FactoryFailureException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/Desktop/CS0320/" +
                "Server/server-nhaseley-sxfiavn/src/main/data/MyCSVs/empty.csv");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("view");

        Moshi moshi = new Moshi.Builder().build();
        ViewHandler.ViewSuccessResponse response =
                moshi.adapter(ViewHandler.ViewSuccessResponse.class).fromJson(
                        new Buffer().readFrom(clientConnection2.getInputStream()));

        Reader fileReader = new FileReader("/Users/nylevenya/Desktop/CS0320/Server/" +
                "server-nhaseley-sxfiavn/src/main/data/MyCSVs/empty.csv");
        parser<List<String>> fileParser = new parser<>();
        CreatorFromRow<List<String>> searchMethod = new SearchMethod();
        List<List<String>> parsedData = fileParser.Parser(fileReader, searchMethod);
        assertEquals(new ViewHandler.ViewSuccessResponse("success", parsedData), response);
        clientConnection2.disconnect();
    }

    @Test
    public void testAPIViewInvalidPath() throws IOException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=invalid.csv");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("view");

        Moshi moshi = new Moshi.Builder().build();
        ViewHandler.ViewFailureResponse response =
                moshi.adapter(ViewHandler.ViewFailureResponse.class).fromJson(
                        new Buffer().readFrom(clientConnection2.getInputStream()));
        assertEquals(new ViewHandler.ViewFailureResponse("failure", "error_bad_request",
                "No non-empty CSV data has been successfully loaded yet."), response);
        clientConnection2.disconnect();
    }


    @Test
    public void testAPISearchCSV() throws IOException, FactoryFailureException, InvalidSearchException {
        HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/Desktop/CS0320/" +
                "Server/server-nhaseley-sxfiavn/src/main/data/census/dol_ri_earnings_disparity.csv");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("search?searchTerm=Black&allColumns=false" +
                "&hasHeaders=true&inputColumn=Race&indexOrHeader=heAder");

        Moshi moshi = new Moshi.Builder().build();
        SearchHandler.SearchSuccessResponse response =
                moshi.adapter(SearchHandler.SearchSuccessResponse.class).fromJson(
                        new Buffer().readFrom(clientConnection2.getInputStream()));

        Reader fileReader = new FileReader("/Users/nylevenya/Desktop/CS0320/Server/" +
                "server-nhaseley-sxfiavn/src/main/data/census/dol_ri_earnings_disparity.csv");
        parser<List<String>> fileParser = new parser<>();
        CreatorFromRow<List<String>> searchMethod = new SearchMethod();
        List<List<String>> parsedData = fileParser.Parser(fileReader, searchMethod);
        Searcher searchObject = new Searcher();
        searchObject.search(
                parsedData, "Black", false, true, "Data Type", HEADER);
        List<List<String>> searchResponse = searchObject.row_content;

        HashMap<String, Object> paramsDict = new HashMap<String, Object>();
        paramsDict.put("searchTerm", "Black");
        paramsDict.put("allColumns", false);
        paramsDict.put("hasHeaders", true);
        paramsDict.put("inputColumn", "Data Type");
        paramsDict.put("indexOrHeader", HEADER);
        assertEquals(new SearchHandler.SearchSuccessResponse("success", paramsDict, searchResponse), response);
        clientConnection2.disconnect();
    }
//
//    // TODO: EXPECTED BEHAVIOR SUCCESS => failure
//    // ADJUST TO EXPECT ERROR
    @Test
    public void testAPISearchEmptyCSV() throws IOException, FactoryFailureException, InvalidSearchException {
        // search for empty string in the csv
        HttpURLConnection clientConnection = tryRequest("load?filePath=/Users/nylevenya/Desktop/CS0320/" +
                "Server/server-nhaseley-sxfiavn/src/main/data/MyCSVs/empty.csv");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("search?searchTerm=&allColumns=true" +
                "&hasHeaders=true&inputColumn=&indexOrHeader=heAder");

        Moshi moshi = new Moshi.Builder().build();
        SearchHandler.SearchSuccessResponse response =
                moshi.adapter(SearchHandler.SearchSuccessResponse.class).fromJson(
                        new Buffer().readFrom(clientConnection2.getInputStream()));

        Reader fileReader = new FileReader("/Users/nylevenya/Desktop/CS0320/Server/" +
                "server-nhaseley-sxfiavn/src/main/data/MyCSVs/empty.csv");
        parser<List<String>> fileParser = new parser<>();
        CreatorFromRow<List<String>> searchMethod = new SearchMethod();
        List<List<String>> parsedData = fileParser.Parser(fileReader, searchMethod);
        Searcher searchObject = new Searcher();
        searchObject.search(
                parsedData, "", true, true, "", HEADER);
        List<List<String>> searchResponse = searchObject.row_content;

        Map<String, Object> paramsDict = new HashMap<String, Object>();
        paramsDict.put("searchTerm", "");
        paramsDict.put("allColumns", true);
        paramsDict.put("hasHeaders", true);
        paramsDict.put("inputColumn", "");
        paramsDict.put("indexOrHeader", HEADER);
        assertEquals(new SearchHandler.SearchFailureResponse(paramsDict, "error_bad_json", "Error during search process. This csv file is empty."), response);

        clientConnection2.disconnect();

        // search for a value not in the csv
        HttpURLConnection clientConnection3 = tryRequest("search?searchTerm=&allColumns=true" +
                "&hasHeaders=true&inputColumn=&indexOrHeader=heAder");
        Searcher searchObject3 = new Searcher();
        searchObject3.search(
                parsedData, "not-in-csv", true, true, "", HEADER);
        List<List<String>> searchResponse3 = searchObject3.row_content;
        Moshi moshi3 = new Moshi.Builder().build();
        SearchHandler.SearchSuccessResponse response3 =
                moshi3.adapter(SearchHandler.SearchSuccessResponse.class).fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
        assertEquals(new SearchHandler.SearchSuccessResponse("success", paramsDict, searchResponse3), response3);

        clientConnection3.disconnect();
    }

//    @Test
//    public void testAPISearchInvalidPath() throws IOException {
//        HttpURLConnection clientConnection = tryRequest("load?filePath=invalid.csv");
//        clientConnection.disconnect();
//        HttpURLConnection clientConnection2 = tryRequest("view");
//
//        Moshi moshi = new Moshi.Builder().build();
//        SearchHandler.SearchFailureResponse response =
//                moshi.adapter(SearchHandler.SearchFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
//        assertEquals(new SearchHandler.SearchFailureResponse("failure", "error_bad_request", "No non-empty CSV data has been successfully loaded yet."), response);
//        clientConnection2.disconnect();
//    }

    @Test
    public void testBroadBandEmptyInputs() throws IOException {

        HttpURLConnection clientConnection = tryRequest("broadband?state=&county=");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandFailureResponse response =
            moshi.adapter(BroadBandHandler.BroadBandFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

        BroadBandFailureResponse responseB = new BroadBandHandler.BroadBandFailureResponse("failure", "error_bad_request",
            "No State Entered");

        assertEquals(responseB, response);

        clientConnection2.disconnect();
    }

    @Test
    public void testBroadBandEmptyState() throws IOException {
        HttpURLConnection clientConnection = tryRequest("broadband?state=&county=New Haven County");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandFailureResponse response =
            moshi.adapter(BroadBandHandler.BroadBandFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

        BroadBandFailureResponse responseB = new BroadBandHandler.BroadBandFailureResponse("failure", "error_bad_request",
            "No State Entered");

        assertEquals(responseB, response);

        clientConnection2.disconnect();

    }

    @Test
    public void testBroadBandEmptyCounty() throws IOException {
        HttpURLConnection clientConnection = tryRequest("broadband?state=Connecticut&county=New Haven County");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandFailureResponse response =
            moshi.adapter(BroadBandHandler.BroadBandFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

        BroadBandFailureResponse responseB = new BroadBandHandler.BroadBandFailureResponse("failure", "error_bad_request",
            "No County Entered");

        assertEquals(responseB, response);

        clientConnection2.disconnect();

    }

    @Test
    public void testBroadBandInvalidState() throws IOException {
        HttpURLConnection clientConnection = tryRequest("broadband?state=A&county=New Haven County");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandFailureResponse response =
            moshi.adapter(BroadBandHandler.BroadBandFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

        BroadBandFailureResponse responseB =
            new BroadBandHandler.BroadBandFailureResponse(
                "failure",
                "error_bad_request",
                "State Input Not Found");

        assertEquals(responseB, response);

        clientConnection2.disconnect();
    }

    @Test
    public void testBroadBandInvalidCounty() throws IOException {
        HttpURLConnection clientConnection = tryRequest("broadband?state=Connecticut&county=A");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandFailureResponse response =
            moshi.adapter(BroadBandHandler.BroadBandFailureResponse.class).fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

        BroadBandFailureResponse responseB =
            new BroadBandHandler.BroadBandFailureResponse(
                "failure",
                "error_bad_request",
                "County Input Not Found");

        assertEquals(responseB, response);

        clientConnection2.disconnect();

    }

    @Test
    public void testBroadBandValidInput() throws IOException {
        HttpURLConnection clientConnection = tryRequest("broadband?state=Connecticut&county=New Haven County");
        clientConnection.disconnect();
        HttpURLConnection clientConnection2 = tryRequest("broadband");

        Moshi moshi = new Moshi.Builder().build();
        BroadBandHandler.BroadBandSuccessResponse response =
            moshi.adapter(BroadBandHandler.BroadBandSuccessResponse.class).fromJson(new
                Buffer().readFrom(clientConnection2.getInputStream()));

        HashMap<String, String> paramsDict = new HashMap<>();
        paramsDict.put("county", "New Haven County");
        paramsDict.put("state", "Connecticut");

        LocalDateTime currTime = java.time.LocalDateTime.now();

        BroadBandSuccessResponse responseB =
            new BroadBandHandler.BroadBandSuccessResponse(
                paramsDict,
                "87.3",
                currTime);

        assertEquals(responseB, response);

        clientConnection2.disconnect();


    }

}