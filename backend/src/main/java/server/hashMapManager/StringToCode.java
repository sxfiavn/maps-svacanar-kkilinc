package server.hashMapManager;

import java.util.HashMap;
import java.util.List;
import server.caching.FetchApiData;

/**
 * A class for populating and managing mappings of state names to state codes and county
 * names to county codes.
 */
public class StringToCode {

  // TODO: Test each method

  /** A map that stores mappings of state names to state codes. */
  private final HashMap<String, String> StateMap;

  /** A map that stores mappings of state codes to mappings of county names to county codes. */
  private final HashMap<String, HashMap<String, String>> CountyMap;

  /** Constructs a new StringToCode object and populates the StateMap and CountyMap. */
  public StringToCode() {
    this.StateMap = new HashMap<>();
    this.StateMap_Populate();

    this.CountyMap = new HashMap<>();
    this.CountyMap_Populate();
  }

  /**
   * Populates the StateMap with state names and state codes.
   */
  private void StateMap_Populate(){
    // Call API with State Codes and Load Data
    FetchApiData api_call = new FetchApiData("https://api.census.gov/data/2010"
        + "/dec/sf1?get=NAME&for=state:*");
    List<List<String>> parsed_api = api_call.getData();

    // parse through API to get state - code
    // Assuming API format will always be ok -> code will always be numbers, will never be null
    for (int i = 1; i < parsed_api.size(); i++) { //i = 1, i=0 is headers
      List<String> row = parsed_api.get(i);
      String state = row.get(0);
      String code = row.get(1);
      StateMap.put(state, code);
    }
  }

  /**
   * Populates the CountyMap with county names, state codes, and county codes.
   */
  private void CountyMap_Populate() {

    // Call API with County Codes and Load Data
    FetchApiData api_call = new FetchApiData("https://api.census.gov/data/2010"
        + "/dec/sf1?get=NAME&for=county:*");
    List<List<String>> parsed_api = api_call.getData();

    for (int i = 1; i < parsed_api.size(); i++) { //i = 1, i=0 is headers
      List<String> row = parsed_api.get(i);

      String countyState = row.get(0);
      String[] parts = countyState.split(",");
      String county_name = parts[0];

      String state_code = row.get(1);
      String county_code = row.get(2);

      // Check if the state code is already in CountyMap
      if (!CountyMap.containsKey(state_code)) {
        CountyMap.put(state_code, new HashMap<>());
      }

      // Add the county information to CountyMap
      CountyMap.get(state_code).put(county_name, county_code);

    }
  }

  /**
   * Getter fot StateMap - Mapping State names to their Code
   * @return StateMap
   */
  public HashMap<String, String> GetStateMap(){
    return StateMap;
  }

  /**
   * Getter fot CountyMap - Mapping State codes to a hashmap of county name to their Code
   * @return CountyMap
   */
  public HashMap<String, HashMap<String, String>> GetCountyMap(){
    return CountyMap;
  }

}
