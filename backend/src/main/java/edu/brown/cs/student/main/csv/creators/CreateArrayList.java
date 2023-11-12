package edu.brown.cs.student.main.csv.creators;

import java.util.ArrayList;
import java.util.List;

/** This class converts each row of the CSV into an arrayList of strings. */
public class CreateArrayList implements CreatorFromRow<ArrayList<String>> {

  public CreateArrayList() {}

  /**
   * This is a method that creates an ArrayList representation of the passed in row by simply
   * wrapping it in an array list, then returning that as an output.
   *
   * @param row row from the Parser class in list form
   * @return a wrapper ArrayList that wraps the inputted List (row) so that it can be iterated over
   *     and indexed into in the Searcher class
   */
  @Override
  public ArrayList<String> create(List<String> row) {
    // just wrap the inputted list -> array list of strings for flexible access
    return new ArrayList<>(row); // no try catch here because will always assume List input
  }
}
