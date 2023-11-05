package server.csv.parse;

/**
 * @author sofia vaca narvaja
 * @version 1
 *     <p>This interface defines a rule for converting a list of strings (row) into type T. Classes
 *     that implement this interface will need to provide an implementation of the create method. In
 *     the case of failure or an error in the creation proces, they can throw
 *     FactoryFailureException.
 * @param <T> Target type. Row will be converted to T
 */
public interface CreatorFromRow<T> {
  /**
   * Converts a list of strings (row) into an object of type T.
   *
   * @param row The list of strings to be converted.
   * @return Object of type T created from the input row.
   * @throws FactoryFailureException If there is an error or failure.
   */
  T create(String[] row) throws FactoryFailureException;
}
