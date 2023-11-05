package UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.caching.ApiCall;

import static org.junit.jupiter.api.Assertions.*;


public class TestApiCall {

  private ApiCall apiCall;

  @BeforeEach
  public void setUp() {
    // Initialize the ApiCall object with test values
    apiCall = new ApiCall(
        "26",
        "155",
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_"
            + "C03_022E&for=county:*&in=state:*");
  }

  @Test
  public void testObtainPercentage() {
    // Test that ObtainPercentage method returns the expected percentage value
    String percentage = apiCall.GetPercentage();
    assertNotNull(percentage, "Percentage should not be null");
    assertFalse(percentage.isEmpty(), "Percentage should not be empty");
    assertEquals("81.5", percentage, "Expected percentage value");
  }

  // TODO: Need to mock data to test an invalid URL as error arises from there
//  @Test
//  public void testObtainPercentageWithInvalidURL() {
//    // Test ObtainPercentage method with an invalid URL
//    ApiCall invalidApiCall = new ApiCall("26", "155", "https://invalid.api/url");
//    String percentage = invalidApiCall.GetPercentage();
//    assertNull(percentage, "Percentage should be null for an invalid URL");
//  }

  @Test
  public void testObtainPercentageWithEmptyRows() {
    // Test ObtainPercentage method with empty rows
    ApiCall emptyRowsApiCall = new ApiCall("99", "999", "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:*&in=state:*");
    String percentage = emptyRowsApiCall.GetPercentage();
    assertEquals("EMPTY ROW", percentage, "Percentage should be 'EMPTY ROW' for empty rows");
  }
}
