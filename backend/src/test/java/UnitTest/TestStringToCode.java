package UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.hashMapManager.StringToCode;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStringToCode {

  private StringToCode stringToCode;

  @BeforeEach
  public void setUp() {
    // Initialize the StringToCode object before each test
    stringToCode = new StringToCode();
  }

  @Test
  public void testStateMapPopulated() {
    // Test that the StateMap is not empty
    HashMap<String, String> state_map = stringToCode.GetStateMap();
    assertFalse(state_map.isEmpty(), "StateMap should not be empty");
  }

  @Test
  public void testCountyMapPopulated() {
    // Test that the CountyMap is not empty
    HashMap<String, HashMap<String, String>> county_map = stringToCode.GetCountyMap();
    assertFalse(county_map.isEmpty(), "CountyMap should not be empty");
  }

  @Test
  public void testStateMapContainsData() {
    // Test that the StateMap contains specific data
    HashMap<String, String> state_map = stringToCode.GetStateMap();
    assertEquals("01", state_map.get("Alabama"), "StateMap should contain Alabama");
    assertEquals("02", state_map.get("Alaska"), "StateMap should contain Alaska");
    // Add more test cases as needed
  }

  @Test
  public void testCountyMapContainsData() {
    // Test that the CountyMap contains specific data
    HashMap<String, HashMap<String, String>> county_map = stringToCode.GetCountyMap();
    assertTrue(county_map.containsKey("01"), "CountyMap should contain state code '01'");
    assertTrue(county_map.get("01").containsKey("Autauga County"), "CountyMap should contain Autauga County in Alabama");
    // Add more test cases as needed
  }
}
