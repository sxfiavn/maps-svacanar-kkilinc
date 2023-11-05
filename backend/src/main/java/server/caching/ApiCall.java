package server.caching;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to load data and get percentage for BroadBandHandler
 */
public class ApiCall {
  private final String state_code;
  private final String county_code;

  private final String url;

  private final String percentage;

  /**
   * Constructor with state and county maps
   */
  public ApiCall(String StateCode, String CountyCode, String URL){

    state_code = StateCode;
    county_code = CountyCode;
    url = URL;
    percentage = ObtainPercentage();
  }

  /**
   * Method to be invoked if the API request is not in the Hashmap.
   * Call API with Fetch API Data
   * Return List<List<String>> with a single row.
   *
   * @param URL for the API
   * @return List<List<String>> Rows matching state and county
   */
  private List<List<String>> parsed_searched(String URL) {
      List<List<String>> rows = new ArrayList<>();

      // Create Instance of FetchAPIData + get the list<list<Strings>>
      List<List<String>> api_data = new FetchApiData(URL).getData();

      if (api_data == null){
        rows = null;
      }

      // Finding the row with the matching state and county codes
      for (int i = 1; i < api_data.size(); i++) { //i = 1, i=0 is headers
        List<String> row = api_data.get(i);

        String state = row.get(2);
        String county = row.get(3);

        if (state.equals(state_code) && county.equals(county_code)){
          rows.add(row);
        }
      }

      // Should only have one entry, only one value per county in API
      // row should look like [[county, state, code, code, percentage]]
      return rows;
  }

  /**
   * From a row for a single county, get the percentage value of the state, county
   * @return Percentage (String)
   */
  private String ObtainPercentage() {

    List<List<String>> rows = parsed_searched(url);

    if (rows == null) {
      return null;
    }
    if (rows.isEmpty()){
      return "EMPTY ROW";
    }

    return rows.get(0).get(1);
  }

  /**
   * Getter for percentage field in constructor
   * @return percentage (String)
   */
  public String GetPercentage() {
    return percentage;
  }

}
