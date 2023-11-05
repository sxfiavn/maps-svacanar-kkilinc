package server; // package edu.brown.cs32.examples.moshiExample.server;


import static spark.Spark.after;

import com.google.common.cache.Cache;
import java.util.HashMap;
import java.util.List;

import server.caching.ApiCallCache;
import server.hashMapManager.StringToCode;
import spark.Spark;

/**
 * Contains the main() method which starts Spark and runs the various handlers.
 * Calls StringToCode to get hashMaps with State/County Codes
 * Endpoint to allow user to load a CSV
 * Endpoint to allow user to view data from the most recently loaded CSV
 * Endpoint to allow user to search for rows containing a given search term from the most recently loaded CSV
 * Endpoint to allow user to retrieve the percentage of broadband access for a given county and state in the US
 */
public class Server {
  public static List<List<String>> csvLoad;

  public static List<List<String>> getLoad(){
    return csvLoad;
  }

  public static void setLoad(List<List<String>> toSet){
    csvLoad = toSet;
  }

  public static void main(String[] args) {
    int port = 3000;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Preprocessing Necessary Data For BroadBandHandler
    // HashMaps: K = State/County Name, V = State/County Code.
    StringToCode string_code = new StringToCode();
    HashMap<String, String> StateCodeMap = string_code.GetStateMap();
    HashMap<String, HashMap<String, String>> CountyCodeMap = string_code.GetCountyMap();
    Cache<String, String> cache = new ApiCallCache().getCache();

    // Setting up the handler for the GET /order and /mock endpoints
    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(StateCodeMap, CountyCodeMap, cache));

    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that? -> we aren't indicating an endpoint
    System.out.println("Server started at http://localhost:" + port);
  }
}
