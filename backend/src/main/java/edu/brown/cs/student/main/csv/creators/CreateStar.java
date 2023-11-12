package edu.brown.cs.student.main.csv.creators;

import edu.brown.cs.student.main.csv.FactoryFailureException;
import java.util.List;

/**
 * The CreateStar class of this project. This is a STARS class that implements CreatorFromRow. It is
 * suitable for a CSV that contains StarID, Proper Name, X, Y, Z as column names
 */
public class CreateStar implements CreatorFromRow<CreateStar> {
  private List<String> row;
  private int starId;
  private String properName;
  private double x1;
  private double y1;
  private double z1;

  /** The DataTypeB default constructor takes no arguments. */
  public CreateStar() {}

  /**
   * The DataTypeB constructor takes in a row.
   *
   * @param row - a list of strings
   */
  public CreateStar(List<String> row) throws FactoryFailureException {
    if (row.size() != 5) {
      throw new FactoryFailureException("Incorrect number of columns", row);
    }
    this.row = row;
    this.starId = Integer.parseInt(row.get(0));
    this.properName = row.get(1);
    this.x1 = Double.parseDouble(row.get(2));
    this.y1 = Double.parseDouble(row.get(3));
    this.z1 = Double.parseDouble(row.get(4));
  }

  /**
   * The create method.
   *
   * @param row - a list of strings
   * @return - an object of type DataTypeA
   * @throws FactoryFailureException - when an empty row is given
   */
  @Override
  public CreateStar create(List<String> row) throws FactoryFailureException {
    return new CreateStar(row);
  }

  /**
   * Changes the row into a string object with its name in front.
   *
   * @return - a string of the rows contents.
   */
  public String toString() {
    return "StarID: "
        + this.starId
        + ", Proper Name: "
        + this.properName
        + ", X: "
        + this.x1
        + ", Y: "
        + this.y1
        + ", Z: "
        + this.z1;
  }

  /**
   * The get method.
   *
   * @param index - an integer representing the row of interest
   * @return - String of the object at that location
   */
  public String get(int index) {
    return this.row.get(index);
  }

  /**
   * The contains method checks if a string is in the row.
   *
   * @param str - a string representing the target.
   * @return - a boolean of whether the string is in the row.
   */
  public boolean contains(String str) {
    return this.row.contains(str);
  }

  /**
   * The size method returns the size of the row.
   *
   * @return - an integer of the size of the row.
   */
  public int size() {
    return this.row.size();
  }
}
