package edu.brown.cs.student.main.server.datasource.broadband_no_caching;

import edu.brown.cs.student.main.server.DatasourceException;

/** An interface for retrieving broadband data. */
public interface BroadbandDataSource {

  /**
   * Retrieves broadband data based on state and county codes and names.
   *
   * @param stateCode The code representing the state.
   * @param countyCode The code representing the county within the specified state.
   * @return BroadbandData The broadband data associated with the specified state and county.
   * @throws DatasourceException when an error occurs while retrieving the data.
   */
  BroadbandData getBroadbandData(String stateCode, String countyCode) throws DatasourceException;
}
