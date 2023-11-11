package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.csv.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** This class converts each row in the CSV into a list of booleans. */
public class CreateBoolean implements CreatorFromRow<List<Boolean>> {

  public CreateBoolean() {}

  /**
   * This converts a passed in row to a list of booleans, using case-insensitive matching for
   * booleans (Java's naming convention) to constrain the otherwise lax criteria set by
   * .parseBoolean(string)
   *
   * @param row passed in row parsed from the CSV file, from the Parser class
   * @return a new list object that converts the passed in row to a list of booleans
   * @throws FactoryFailureException an exception thrown if any arbitrary string in the passed in
   *     row is not parseable as a boolean per the conditions set
   */
  @Override
  public List<Boolean> create(List<String> row) throws FactoryFailureException {
    // just wrap the inputted list -> array list of strings for flexible access
    try {
      ArrayList<Boolean> bools = new ArrayList<>();
      for (String s : row) {
        if (!s.isEmpty() && (s.equals("true") || s.equals("false"))) {
          Boolean b = Boolean.parseBoolean(s); // convert the string into a boolean
          bools.add(b); // add to the list of booleans
        } else {
          throw new IllegalArgumentException(
              "Reformat boolean string to be: true or false, for the following input string. "
                  + "If nothing is shown, the string passed was empty: "
                  + s);
        }
      }
      return bools;
    } catch (IllegalArgumentException i) {
      throw new FactoryFailureException(
          "Inputted row has a string that is in a invalid boolean format: " + i.getMessage(), row);
    }
  }
}
