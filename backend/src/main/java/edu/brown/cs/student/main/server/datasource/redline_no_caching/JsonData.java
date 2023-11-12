package edu.brown.cs.student.main.server.datasource.redline_no_caching;

import java.util.ArrayList;

public record JsonData(
    ArrayList<ArrayList<String>> data, String errorMessage, String retrievalTime) {

  /**
   * Factory method of BroadbandData for error scenarios.
   *
   * @param errorMessage A string with the error message.
   * @return A JsonData instance with the error message and null for all other fields.
   */
  public static JsonData forErrorMessage(String errorMessage, String time) {
    return new JsonData(null, errorMessage, time);
  }

  /**
   * Factory method of BroadbandData that separates the given List into its respective name,
   * percentage, state, and county data.
   *
   * @param jsonDataValue A nested list containing json data.
   * @return A BroadbandData instance with the given data.
   */
  public static JsonData fromData(ArrayList<ArrayList<String>> jsonDataValue, String time) {
    if (jsonDataValue != null && jsonDataValue.size() >= 2) {
      return new JsonData(jsonDataValue, null, time);
    }
    return JsonData.forErrorMessage("No data returned from API", time);
  }
}
