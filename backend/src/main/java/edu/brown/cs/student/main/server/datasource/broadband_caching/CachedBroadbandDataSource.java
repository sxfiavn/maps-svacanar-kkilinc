package edu.brown.cs.student.main.server.datasource.broadband_caching;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.DatasourceException;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandData;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandDataSource;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import edu.brown.cs.student.main.server.utilities.MoshiUtils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The class allows the server to have a caching functionality. The wrapped source is a
 * BroadbandDataSource type.
 */
public class CachedBroadbandDataSource implements BroadbandDataSource {
  private final BroadbandDataSource wrappedSource;
  private final LoadingCache<String, BroadbandData> cache;
  private final LoadingCache<String, List<List<String>>> countyNamesToCodesCache;

  /**
   * Method that wraps a BroadbandDataSource so that you can cache responses for efficiency.
   *
   * @param toWrap BroadbandData object (can be mocked or not) that can used to retrieve broadband
   *     data for specified query parameters.
   * @param maxSize The maximum number of elements that the cache can hold.
   * @param expirationMinutes The maximum amount of time that items can be held in the cache.
   */
  public CachedBroadbandDataSource(BroadbandDataSource toWrap, int maxSize, int expirationMinutes) {
    this.wrappedSource = toWrap;

    this.countyNamesToCodesCache =
        CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public List<List<String>> load(String stateCode) throws DatasourceException {
                    return getCountyNamesToCodes(stateCode);
                  }
                });

    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @Override
                  public BroadbandData load(String key) throws DatasourceException {
                    String[] parts =
                        key.split(
                            "_"); // See retrieval request below (formatted with fields separated by
                    // an underscore).
                    // Didn't need to have the extraneous state -> code mapping here, since
                    // getBroadBandData does that for us.
                    return wrappedSource.getBroadbandData(parts[0], parts[1]);
                  }
                });
  }

  /**
   * Gets the broadband data from the cache and prints out the stats of the cache for reference.
   *
   * @param stateName The code representing the state.
   * @param countyName The code representing the county within the specified state.
   * @return cached data using the state code and county code as retrieval queries.
   */
  @Override
  public BroadbandData getBroadbandData(String stateName, String countyName) {
    return cache.getUnchecked(stateName + "_" + countyName);
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
    } catch (IOException e) {
      throw new DatasourceException("Exception occurred");
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
}
