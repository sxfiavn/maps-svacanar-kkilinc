package server.csv.parse;

import java.util.ArrayList;
import java.util.List;

public class FactoryFailureException extends Exception {

  /** Row of data that caused the failure */
  final List<String> row;

  /**
   * Constructs a new FactoryFailureException. Allows for a specific error message and has the row
   * that triggered the exception
   *
   * @param message Error message explaining the cause of the failure.
   * @param row The row of data that triggered the exception.
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
