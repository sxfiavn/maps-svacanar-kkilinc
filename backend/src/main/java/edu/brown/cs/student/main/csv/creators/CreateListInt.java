package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.csv.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** This class converts each row in the CSV into a list of integers. */
public class CreateListInt implements CreatorFromRow<List<Integer>> {

  public CreateListInt() {}

  /**
   * This method takes in a row from the Parser in list form, iterates over it, converts each string
   * to its integer counterpart if possible, then appends the elements to a new list, which is then
   * returned.
   *
   * @param row row passed in from the Parser in list form
   * @return a list of integers that the original row was converted to
   * @throws FactoryFailureException if any arbitrary string in the passed in row cannot be
   *     parsed/converted into an integer
   */
  @Override
  public List<Integer> create(List<String> row) throws FactoryFailureException {

    // just wrap the inputted list -> array list of strings for flexible access
    ArrayList<Integer> ints = new ArrayList<>();
    try {
      for (String s : row) {
        Integer i = Integer.parseInt(s);
        ints.add(i);
      }
    } catch (NumberFormatException nf) {
      throw new FactoryFailureException(
          "Inputted row has strings that can't be parsed into an integer: " + nf.getMessage(), row);
    }
    return ints;
  }
}
