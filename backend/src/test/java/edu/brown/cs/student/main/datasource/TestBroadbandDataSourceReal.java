package edu.brown.cs.student.main.datasource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.brown.cs.student.main.server.DatasourceException;
import edu.brown.cs.student.main.server.datasource.broadband_caching.CachedBroadbandDataSource;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.BroadbandData;
import edu.brown.cs.student.main.server.datasource.broadband_no_caching.CensusAPIBroadbandSource;
import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This is a test suite that ACTUALLY queries the census API server in order to test the methods in
 * the datasource classes. It is split off from the mock datasource classes that are used for
 * integration testing, since exception handling etc. needed to be tested explicitly.
 */
public class TestBroadbandDataSourceReal {

  /**
   * Runs before ALL testcases. We should set this up so that we can check the contents and use them
   * later on.
   */
  @BeforeAll
  public static void setupBeforeEverything() throws DatasourceException {
    DataStorage.setStateNamesToCodes(DataStorage.apiStateNamesToCodes());
  }

  /**
   * This method tests if the nested lists returned from the DataStorage class is correct and can be
   * used for querying. Since the important part is that we get back a list of lists for ALL states
   * and not just a few, we'll just check the size here instead of each and every individual entry.
   * Also check the header, since that's unique to the query.
   */
  @Test
  public void testStateNamesToCodes() {

    // Check size of the returned output
    List<List<String>> realStorage = DataStorage.getStateNamesToCodes();
    Assertions.assertEquals(53, realStorage.size()); // Manually counted the rows to verify.

    // Check that the header is of the form [Name, State] for the queried fields.
    Assertions.assertEquals("NAME", realStorage.get(0).get(0));
    Assertions.assertEquals("state", realStorage.get(0).get(1));
  }

  /**
   * This method tests the real API datasource for real broadband data, queried for with cases of
   * state and county names that are correct + spelled right; correct + with strange syntax.
   * Inspired mostly by the livecode stencil for the nws api.
   *
   * @throws DatasourceException Since the getBroadbandData method has this in its signature, but
   *     nothing will get thrown here.
   */
  @Test
  public void testBroadbandDataCanLoad_REAL() throws DatasourceException {

    // This is the regular broadband data source class - also separately queried in case the cache
    // doesn't have the requested entry.
    CensusAPIBroadbandSource source = new CensusAPIBroadbandSource();

    // Retrieve Broadband data object for a sample state and county that DO exist.
    BroadbandData realBroadBand1 = source.getBroadbandData("California", "Alameda County");

    // Check the output is not null.
    assertNotNull(realBroadBand1);

    // Check if each of the fields are as expected.
    Assertions.assertEquals("Alameda County, California", realBroadBand1.name());
    Assertions.assertEquals("89.9", realBroadBand1.percentage());
    Assertions.assertEquals("06", realBroadBand1.state());
    Assertions.assertEquals("001", realBroadBand1.county());
    assertNull(realBroadBand1.errorMessage());

    // Test that case-insensitive matching still applies in the case of mistyped names, so the user
    // gets data as desired.
    BroadbandData realBroadBand2 = source.getBroadbandData("CaliForNia", "AlaMEDA CounTY");
    Assertions.assertEquals("Alameda County, California", realBroadBand2.name());
    Assertions.assertEquals("89.9", realBroadBand2.percentage());
    Assertions.assertEquals("06", realBroadBand2.state());
    Assertions.assertEquals("001", realBroadBand2.county());
    assertNull(realBroadBand2.errorMessage());
  }

  /**
   * This is a test for a nonexistent state name and county, which should yield NO results. Should
   * throw an error that is later caught in the handler class to return the (buggy) query fields at
   * fault in the response.
   *
   * <p>Tests the helper methods for finding state codes from state names; finding county codes from
   * county names.
   *
   * @throws DatasourceException When receiving queries for states and counties that do not exist.
   */
  @Test
  public void testFakeStateFakeCounty() throws DatasourceException {

    // This is the regular broadband data source class - also separately queried in case the cache
    // doesn't have the requested entry.
    CensusAPIBroadbandSource source = new CensusAPIBroadbandSource();

    // Retrieve Broadband data object for a sample state and county that DO exist, but are mistyped
    // beyond comprehension.
    Assertions.assertThrows(
        DatasourceException.class, () -> source.getBroadbandData("Calivornia", "AlaNEDA CousTY"));

    // Retrieve Broadband data object for a sample state and county that DO NOT exist at all.
    Assertions.assertThrows(
        DatasourceException.class, () -> source.getBroadbandData("Not a State", "Not a County"));

    // Test each of the additional helper methods called by .getBroadBandData to double-check the
    // above error messages.
    String stateCode = source.findStateCode("Calivornia");
    assertNull(stateCode);
    String countyCode =
        source.findCountyCode("AlaNEDA CousTY", "Calivornia", DataStorage.getStateNamesToCodes());
    assertNull(countyCode);

    // If all of the above work, then this is a needless test, but this is just a final check to
    // make sure that THIS helper method is responsible for erroring out and nothing else -> being
    // the sole cause for the DataSource Exception above.
    Assertions.assertThrows(
        DatasourceException.class, () -> source.getCountyNamesToCodes("Not a State"));
  }

  /**
   * Since this user story was removed, these tests just test for some basic functionalities of the
   * cache. These include: (1) checking simply if the cache does the behavior of retrieving data it
   * doesn't have by querying its wrapped object, and (2) if it retrieves the same data after
   * separate queries to it, (3) We can perform a manual check of if it's doing its job of caching
   * since we can inspect the standard output of the caching stats!
   */
  @Test
  public void testCachedOutput() {

    // We'll just initialize the cache to its default parameters. This retrieves the data for the
    // first time.
    CachedBroadbandDataSource cachedSource =
        new CachedBroadbandDataSource(new CensusAPIBroadbandSource(), 1000, 10);
    BroadbandData cachedBroadBand1 = cachedSource.getBroadbandData("California", "Alameda County");
    Assertions.assertEquals("Alameda County, California", cachedBroadBand1.name());
    Assertions.assertEquals("89.9", cachedBroadBand1.percentage());
    Assertions.assertEquals("06", cachedBroadBand1.state());
    Assertions.assertEquals("001", cachedBroadBand1.county());

    // Then, let's retrieve it a second time and check if the output is the same. Though this
    // doesn't check
    // if the items are actually getting cached, it does check for inconsistency between separate
    // queries.
    BroadbandData cachedBroadBand2 = cachedSource.getBroadbandData("California", "Alameda County");
    Assertions.assertEquals("Alameda County, California", cachedBroadBand2.name());
    Assertions.assertEquals("89.9", cachedBroadBand2.percentage());
    Assertions.assertEquals("06", cachedBroadBand2.state());
    Assertions.assertEquals("001", cachedBroadBand2.county());

    // NOTE: We can check that items are actually getting cached if it's the case that the standard
    // output for the cache stats read that it isn't empty!

  }
}
