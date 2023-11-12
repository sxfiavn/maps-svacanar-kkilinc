package edu.brown.cs.student.main.server.datasource.broadband_no_caching;

import java.util.List;

/**
 * Broadband record holds details of the percentage of coverage, state, county information, and
 * possible errors in obtaining the data.
 */
public record BroadbandData(
    String name,
    String percentage,
    String state,
    String county,
    String errorMessage,
    String retrievalTime) {

  /**
   * Factory method of BroadbandData for error scenarios.
   *
   * @param errorMessage A string with the error message.
   * @return A BroadbandData instance with the error message and null for all other fields.
   */
  public static BroadbandData forErrorMessage(String errorMessage, String time) {
    return new BroadbandData(null, null, null, null, errorMessage, time);
  }

  /**
   * Factory method of BroadbandData that separates the given List into its respective name,
   * percentage, state, and county data.
   *
   * @param broadbandDataValue A nested list containing broadband data.
   * @return A BroadbandData instance with the given data.
   */
  public static BroadbandData fromData(List<List<String>> broadbandDataValue, String time) {
    if (broadbandDataValue != null && broadbandDataValue.size() >= 2) {
      List<String> values = broadbandDataValue.get(1);
      return new BroadbandData(
          values.get(0), values.get(1), values.get(2), values.get(3), null, time);
    }
    return BroadbandData.forErrorMessage("No data returned from API", time);
  }
}
