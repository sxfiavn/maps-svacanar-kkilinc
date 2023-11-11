package edu.brown.cs.student.main.server.utilities;

import com.squareup.moshi.Types;
import edu.brown.cs.student.main.geoJsonParser.FeatureCollection;
import edu.brown.cs.student.main.server.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** This class acts as storage for the server. */
public class DataStorage {

  private static ArrayList<ArrayList<String>> currentCSVData;
  private static String filepath;
  private static List<List<String>> stateNamesToCodes;
  private static FeatureCollection currentJsonData;

  /**
   * A getter method for the current json data.
   *
   * @return - A copy of the csv data that is being stored.
   */
  public static FeatureCollection getCurrentJsonData() {
    return currentJsonData;
  }

  /**
   * A setter method for the current csv data.
   *
   * @param data - An array or arrays that contains the csv data.
   */
  public static void setCurrentJsonData(FeatureCollection data) {
    DataStorage.currentJsonData = data;
  }

  /**
   * A getter method for the current csv data.
   *
   * @return - A copy of the csv data that is being stored.
   */
  public static ArrayList<ArrayList<String>> getCurrentCSVData() {
    ArrayList<ArrayList<String>> copy = new ArrayList<>();
    for (ArrayList<String> innerList : currentCSVData) {
      copy.add(new ArrayList<>(innerList));
    }
    return copy;
  }

  /**
   * A setter method for the current csv data.
   *
   * @param data - An array or arrays that contains the csv data.
   */
  public static void setCurrentCSVData(ArrayList<ArrayList<String>> data) {
    currentCSVData = data;
  }

  /**
   * A getter method for the current filepath.
   *
   * @return - The filepath of the currently loaded csv file.
   */
  public static String getFilepath() {
    return filepath;
  }

  /**
   * A setter method for the current filepath.
   *
   * @param path - The path to the file.
   */
  public static void setFilepath(String path) {
    if (!path.endsWith(".csv")) {
      throw new IllegalArgumentException("File path must be a .csv file");
    }
    filepath = path;
  }

  /**
   * A getter method for the current stateNamesToCodes list.
   *
   * @return - A copy of stateNamesToCodes list that is being stored.
   */
  public static List<List<String>> getStateNamesToCodes() {
    return List.copyOf(stateNamesToCodes);
  }

  /**
   * Static method that sets the stateNamesToCodes object. Practices defensive programming because
   * the original object stays private static, but we can set its value so that it can be used
   * elsewhere.
   *
   * @param list - A list containing the stateNamesToCodes.
   */
  public static void setStateNamesToCodes(List<List<String>> list) {
    stateNamesToCodes = list;
  }

  /**
   * Method that returns a nested list that can later be iterated through in order to get the code
   * that corresponds to the queried state name.
   *
   * @return A list of lists representing correspondence between state names and their codes.
   * @throws DatasourceException that is triggered if the API server throws an exception.
   */
  public static List<List<String>> apiStateNamesToCodes() throws DatasourceException {
    try {
      URL requestURL =
          new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
      Type listOfLists = Types.newParameterizedType(List.class, List.class);
      return MoshiUtils.fetchAndDeserialize(requestURL, listOfLists);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw new DatasourceException("Exception occurred");
    }
  }
}
