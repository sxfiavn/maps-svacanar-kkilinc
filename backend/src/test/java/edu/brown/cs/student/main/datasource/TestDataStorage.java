package edu.brown.cs.student.main.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.brown.cs.student.main.server.utilities.DataStorage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** This is a class that tests the functionality of the data storage class. */
public class TestDataStorage {

  /** Resets the starting state. */
  @BeforeEach
  public void setup() {
    DataStorage.setCurrentCSVData(null);
    DataStorage.setStateNamesToCodes(null);
  }

  /**
   * This tests the immutability of loaded csv data. We should be returning an immutable list
   * whenever we call the getter, so modification shouldn't work!
   */
  @Test
  public void testImmutabilityOfCSVData() {
    ArrayList<ArrayList<String>> testData = new ArrayList<>();
    testData.add(new ArrayList<>(Arrays.asList("cali", "texas", "ohio")));
    testData.add(new ArrayList<>(Arrays.asList("1", "2", "3")));
    DataStorage.setCurrentCSVData(testData);
    ArrayList<ArrayList<String>> retrievedData = DataStorage.getCurrentCSVData();
    assertEquals(testData, retrievedData);

    // change the outer array list
    retrievedData.add(new ArrayList<>(Arrays.asList("4", "5", "6")));
    assertNotEquals(retrievedData, DataStorage.getCurrentCSVData());

    // change an inner part of the array
    retrievedData.get(0).add("cheese");
    assertNotEquals(retrievedData, DataStorage.getCurrentCSVData());

    // verify that the original is unchanged
    assertEquals(testData, DataStorage.getCurrentCSVData());
  }

  /** This tests the correct setting and getting of filepaths. */
  @Test
  public void testSetAndGetFilepath() {
    String testPath = "/data/ri_income.csv";
    DataStorage.setFilepath(testPath);
    assertEquals(testPath, DataStorage.getFilepath());

    // incorrect file type
    String testPath2 = "/data/ri_income.pdf";
    assertThrows(IllegalArgumentException.class, () -> DataStorage.setFilepath(testPath2));
  }

  /** This tests the immutability of StateNamesToCodes. */
  @Test
  public void testImmutabilityOfStateNamesToCodes() {
    List<List<String>> testData = new ArrayList<>();
    testData.add(Arrays.asList("Alabama", "01"));
    testData.add(Arrays.asList("Alaska", "02"));
    testData.add(Arrays.asList("Arizona", "04"));

    DataStorage.setStateNamesToCodes(testData);

    List<List<String>> copy = DataStorage.getStateNamesToCodes();
    assertEquals(testData, copy);

    // modifying/attempting to modify the list throws an error
    assertThrows(
        UnsupportedOperationException.class,
        () -> {
          copy.add(Arrays.asList("Idaho", "16"));
        });

    // again check that the original data is unchanged
    assertEquals(testData, DataStorage.getStateNamesToCodes());
  }
}
