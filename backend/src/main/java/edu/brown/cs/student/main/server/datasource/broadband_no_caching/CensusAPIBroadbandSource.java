package edu.brown.cs.student.main.server.datasource.broadband_no_caching;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.DatasourceException;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import edu.brown.cs.student.main.server.utilities.HttpConnectionUtils;
import edu.brown.cs.student.main.server.utilities.MoshiUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import okio.Buffer;

/**
 * This class is responsible for directly sending request to the census and returning the result.
 */
public class CensusAPIBroadbandSource implements BroadbandDataSource {

  /**
   * Gets the broadband data by sending a request to the server, then serializes it into a record.
   *
   * @param stateName The code representing the state.
   * @param countyName The code representing the county within the specified state.
   * @return The server response, serialized into BroadBand data record form.
   * @throws DatasourceException If the API server throws an error.
   */
  @Override
  public BroadbandData getBroadbandData(String stateName, String countyName)
      throws DatasourceException {
    String stateCode;
    String countyCode;

    if (isInteger(stateName)) {
      stateCode = stateName;
    } else {
      stateCode = this.findStateCode(stateName);
    }
    if (isInteger(countyName)) {
      countyCode = countyName;
    } else {
      List<List<String>> countiesToCodes = this.getCountyNamesToCodes(stateCode);
      countyCode = this.findCountyCode(countyName, stateName, countiesToCodes);
    }

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = now.format(formatter);

    try {

      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      HttpURLConnection clientConnection = HttpConnectionUtils.connect(requestURL);

      Moshi moshi = new Moshi.Builder().build();
      Type l = Types.newParameterizedType(List.class, List.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(l);

      // Status code 204: indicates no content is available for the request.
      if (clientConnection.getResponseCode() == 204) {
        System.out.println("HERE??");
        return BroadbandData.forErrorMessage("No data returned from API", formattedDateTime);
      }

      // Get the output from the query
      List<List<String>> broadbandData =
          adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      clientConnection.disconnect();

      // If there isn't any data returned, do the following:
      if (broadbandData == null || broadbandData.size() < 2) {
        throw new DatasourceException(
            "No data returned from API"); // To add necessary field into response hashmap.
        //        return BroadbandData.forErrorMessage("No data returned from API",
        // formattedDateTime);
      }

      return BroadbandData.fromData(broadbandData, formattedDateTime);

    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw new DatasourceException("Malformed JSON");
    } catch (JsonDataException e) {
      System.out.println(e.getMessage());
      throw new DatasourceException("Well-formed JSON, but unexpected format");
    } catch (Exception e) {
      // Any other exception we'll interpret as the ACS API returning another error -> datasource
      // error.
      System.out.println(e.getMessage());
      throw new DatasourceException("No data returned from API");
    }
  }

  /**
   * Iterates over a nested list of state names to codes to find the corresponding state code for
   * the queried state name.
   *
   * @param stateName A string representing the name of the state queried for.
   * @return The corresponding code for the queried state name.
   */
  public String findStateCode(String stateName) {
    String stateCode = null;
    for (List<String> l : DataStorage.getStateNamesToCodes()) {
      if (l.get(0).equalsIgnoreCase(stateName)) {
        stateCode = l.get(1);
        break;
      }
    }
    return stateCode;
  }

  /**
   * Method that gets the corresponding county code from a passed in county name.
   *
   * @param stateCode Code of the state. Used to query for a specific state.
   * @return A nested list that stores the correspondence of county names to codes within a specific
   *     state.
   * @throws DatasourceException if the API server throws an IO exception.
   */
  public List<List<String>> getCountyNamesToCodes(String stateCode) throws DatasourceException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode);
      Type listOfLists = Types.newParameterizedType(List.class, List.class);
      return MoshiUtils.fetchAndDeserialize(requestURL, listOfLists);
    } catch (JsonDataException | IOException e) {
      throw new DatasourceException(
          "Ill-formed request"); // Should be ill-formed request. Caught if Moshi can't deserialize
      // with
      // the requested URL.
    }
  }

  /**
   * Helper method that finds the county code from a list of lists of county names to their codes.
   *
   * @param countyName A string of county name.
   * @param stateName A string of state name.
   * @param countiesToCodes A nested list mapping county names to their codes.
   * @return A string of the country code that matches the given country name.
   */
  public String findCountyCode(
      String countyName, String stateName, List<List<String>> countiesToCodes) {
    String countyCode = null;
    String countyWithName;

    // Standardizes the queries so that they all end with County
    if (countyName.endsWith(" County")) {
      countyWithName = countyName;
    } else {
      countyWithName = countyName + " County";
    }

    for (List<String> l : countiesToCodes) {
      if (l.get(0).equalsIgnoreCase(countyWithName + ", " + stateName)
          || l.get(0).equalsIgnoreCase(countyName + ", " + stateName)) {
        countyCode = l.get(l.size() - 1);
        break;
      }
    }
    return countyCode;
  }

  /**
   * Checks to see if a given string is an integer.
   *
   * @param input - A string representing either an integer or something else.
   * @return - A boolean on whether the input is an integer.
   */
  private boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
