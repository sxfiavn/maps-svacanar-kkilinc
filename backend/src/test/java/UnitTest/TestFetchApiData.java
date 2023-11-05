package UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.caching.FetchApiData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestFetchApiData {

  private FetchApiData fetchApiData;

  @BeforeEach
  public void setUp() {
    // Initialize the FetchApiData object with the test URL
    fetchApiData = new FetchApiData("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:155&in=state:26");
  }

  @Test
  public void testFetchData() {
    // Test that FetchData method returns the expected data for the given URL
    List<List<String>> data = fetchApiData.getData();
    assertNotNull(data, "Fetched data should not be null");
    assertFalse(data.isEmpty(), "Fetched data should not be empty");

    // Verify the expected data structure
    assertEquals(2, data.size(), "Fetched data should have 2 rows");
    assertEquals(4, data.get(0).size(), "First row should have 4 columns");
    assertEquals(4, data.get(1).size(), "Second row should have 4 columns");

    // Verify specific data values
    assertEquals("Shiawassee County, Michigan", data.get(1).get(0), "Expected county name");
    assertEquals("81.5", data.get(1).get(1), "Expected broadband percentage");
    assertEquals("26", data.get(1).get(2), "Expected state code");
    assertEquals("155", data.get(1).get(3), "Expected county code");
  }
}
