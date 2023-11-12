package edu.brown.cs.student.main.server;

/**
 * This is an error provided to catch any error that may occur when a data source exception should
 * occur.
 */
public class DatasourceException extends Exception {

  /**
   * Assigns the message of the error.
   *
   * @param message - The root cause of the error as a string.
   */
  public DatasourceException(String message) {
    super(message);
    Throwable cause = null;
  }
}
