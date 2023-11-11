package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.geoJsonParser.JsonParser;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.CensusAPIBroadbandSource;
import edu.brown.cs.student.main.server.datasource.redline_no_caching.JsonDataSource;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.LoadCsvHandler;
import edu.brown.cs.student.main.server.handlers.FilterHandler;
import edu.brown.cs.student.main.server.handlers.SearchCsvHandler;
import edu.brown.cs.student.main.server.handlers.ViewCsvHandler;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.io.IOException;
import java.util.Scanner;
import spark.Spark;

/** The Main class of our Server project. This is where execution begins. */
public class ServerMain {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) throws DatasourceException, IOException {
    int port = 8080;
    Spark.port(port);

    // Store this mapping of names -> codes, so you don't have to query for it ever again!
    DataStorage.setStateNamesToCodes(DataStorage.apiStateNamesToCodes());
    JsonParser jsonParser = new JsonParser();
    DataStorage.setCurrentJsonData(jsonParser.parse("data/fullDownload.json"));

    // Query parameters for caching
    Scanner myObj = new Scanner(System.in);
    System.out.println(
        "Developer: please input whether or not you want caching functionality for the ACS API: input true for yes caching or false for no caching");
    Boolean yesCache = Boolean.parseBoolean(myObj.nextLine());

    Scanner myObj2 = new Scanner(System.in);
    System.out.println(
        "Developer: please input whether or not you want caching functionality for the GeoJSON services: input true for yes caching or false for no caching");
    Boolean yesCache2 = Boolean.parseBoolean(myObj2.nextLine());

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("loadcsv", new LoadCsvHandler());
    Spark.get("viewcsv", new ViewCsvHandler());
    Spark.get("searchcsv", new SearchCsvHandler());
    Spark.get("broadband", new BroadbandHandler(new CensusAPIBroadbandSource(), yesCache));
    Spark.get("filter", new FilterHandler(new JsonDataSource(), yesCache2));
    Spark.get("searchArea", new SearchAreaHandler());

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
