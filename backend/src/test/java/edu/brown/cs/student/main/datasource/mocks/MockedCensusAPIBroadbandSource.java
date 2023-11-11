package edu.brown.cs.student.main.datasource.mocks;

import edu.brown.cs.student.main.server.DatasourceException;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandData;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandDataSource;

/**
 * Class that mocks the broadband data source class by never actually calling the API, but instead
 * returning a constant broadband data value. Avoids the cost of real API invocations.
 */
public class MockedCensusAPIBroadbandSource implements BroadbandDataSource {
  private final BroadbandData constantData;

  public MockedCensusAPIBroadbandSource(BroadbandData constantData) {
    this.constantData = constantData;
  }

  @Override
  public BroadbandData getBroadbandData(String stateCode, String countyCode)
      throws DatasourceException {
    return this.constantData;
  }
}
