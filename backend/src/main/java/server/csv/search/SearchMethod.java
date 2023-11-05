package server.csv.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import server.csv.parse.CreatorFromRow;
import server.csv.parse.FactoryFailureException;

// this method will be used by the parser in the main function for the search functionality
public class SearchMethod implements CreatorFromRow<List<String>> {

  @Override
  public List<String> create(String[] row) throws FactoryFailureException {

    // Initialize an ArrayList to store the output
    ArrayList<String> outputList = new ArrayList<>();

    if (row != null && row.length > 0) {
      // Copy the elements from the row array to the output list
      Collections.addAll(outputList, row);
      return outputList;
    } else {
      // If the row is null or empty, throw a FactoryFailureException
      throw new FactoryFailureException("Invalid row data", outputList);
    }
  }
}
