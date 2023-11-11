package edu.brown.cs.student.main.csv.creators;

import java.util.List;

/** This class converts each row of the CSV into a string. */
public class CreateListString implements CreatorFromRow<String> {

  public CreateListString() {}

  /**
   * This is a method that will take in a row from the Parser in list form, then iterate over it,
   * concatenate the elements, and return the combined String output in one continuous line.
   *
   * @param row row passed in from the Parser
   * @return a String that represents just the concatenation of the elements of the passed in list
   */
  @Override
  public String create(List<String> row) {
    // first wrap the inputted list -> array list of strings for flexible access to elements
    StringBuilder out =
        new StringBuilder(); // StringBuilder is much more memory efficient than just concatenating
    // strings
    for (int i = 0; i < row.size(); i++) {
      if (i == 0) { // each new row (doesn't have extra comma)
        out.append(row.get(i));
      } else {
        out.append(", ").append(row.get(i)); // just append each string to the final output string
      }
    }
    return out
        .toString(); // no try catch here because i'll just assume there's a string input no matter
  }
}
