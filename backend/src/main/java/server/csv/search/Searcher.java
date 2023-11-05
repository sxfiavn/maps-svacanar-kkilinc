package server.csv.search;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import server.csv.search.InvalidSearchException;

/**
 * The Searcher class is responsible for performing searches in a CSV file, and provides methods to
 * do so.
 */
public class Searcher {

  /**
   * A HashMap that stores the row number and content of rows containing the target string. The key
   * is the row number, and the value is the content of the row as a list of strings.
   */
  public HashMap<Integer, List<String>> rows_to_content = new HashMap<>();

  /**
   * A List that stores the content of rows containing the target content. Each element in the list
   * represents a row as a list of strings.
   */
  public List<List<String>> row_content = new ArrayList<>();

  /**
   * Method will evaluate the parameters and call each helper function accordingly to search for the
   * target string within the list of rows.
   *
   * @param parsed_rows List of rows (lists) of Strings previously parsed from a CSV file.
   * @param initial_target Target string to look for in each row.
   * @param all_columns Boolean indicating if we should look for the target string in all columns.
   * @param hasHeaders Boolean indicating if CSV file has headers or not.
   * @param given_column Parameter indicating a particular column to look for the string in. Type T
   *     could be Null, String or Integer, method will respond accordingly.
   */
  public void search(
      List<List<String>> parsed_rows,
      String initial_target,
      Boolean all_columns,
      Boolean hasHeaders,
      String given_column,
      ColumnSearchOption index_or_header) throws InvalidSearchException {
    if (parsed_rows == null || parsed_rows.isEmpty()){
      throw new RuntimeException("This csv file is empty.");
    }

    // If all_columns is true, or if no given_column is specified: search all columns
    if (all_columns || given_column == null) {
      search_all(parsed_rows, initial_target);
    } else { // if looking at a particular column

      // If given_column has a value, but they aren't specifying if index or header lookup
      if (index_or_header == null) {
        throw new InvalidSearchException(
            "Please indicate whether you are using an index or a header to search the column.");
      }

      Integer header_index = null;

      // If given_column is a column index
      if (index_or_header.equals(ColumnSearchOption.INDEX)) {
        try {
          header_index = parseInt(given_column);
        }

        // Will fail if the given_column is a column header
        catch (NumberFormatException e) { // If given_column is a column index
          throw new InvalidSearchException("Invalid column index: " + given_column);
        }
      }

      // if index_or_header is not index, then it can only be header -> we can work under
      // that assumption
      if (hasHeaders) {

        // Search for the index of that header in the first row

        // Get first row with headers
        List<String> headers = parsed_rows.get(0);
        System.out.print(headers);
        // For every header, check if it matches the given_column
        for (int i = 0; i < headers.size(); i++) {
          String header = headers.get(i);
          if (header.equalsIgnoreCase(given_column)) {
            header_index = i + 1;
          }
        }
      } else {
        // If the CSV has no headers but the user is trying to look into a column with a header.
        throw new InvalidSearchException(
            "Cannot search by header in a CSV file without headers. "
                + "Please provide a valid column index instead.");
      }

      // If no header matched the given_column, throw runtime exception.
      if (header_index == null) {
        throw new RuntimeException("This header doesn't exist in the CSV file");
      }
      search_with_index(parsed_rows, initial_target, header_index);
    }
  }

  /**
   * Helper method to search for the target string in a particular column within a row.
   *
   * @param parsed_rows List of rows to iterate and look for the target string. Rows are lists of *
   *     strings.
   * @param target_string String to search for in each row.
   * @param column_index Index of specific column to search target string in.
   */
  private void search_with_index(
      List<List<String>> parsed_rows, String target_string, Integer column_index)
      throws InvalidSearchException {

    for (int row_index = 0; row_index < parsed_rows.size(); row_index++) {
      List<String> row = parsed_rows.get(row_index);

      // If index is out of range, throw runtime exception
      // Will only happen if manually entered index, not if found through header value
      if (column_index - 1 >= row.size()) {
        throw new InvalidSearchException("The column index is out of range");
      }

      // Obtain value for that column in each row and make it lowercase
      String column_value = row.get(column_index - 1);
      if (column_value.toLowerCase().contains(target_string.toLowerCase())) {
        rows_to_content.put(row_index, row);
        row_content.add(row);
      }
    }
  }

  /**
   * Helper method to search for the target string in all columns for every row.
   *
   * @param parsed_rows List of rows to iterate and look for the target string. Rows are lists of
   *     strings.
   * @param target_string String to search for in each row.
   */
  private void search_all(List<List<String>> parsed_rows, String target_string) {

    // For every row
    for (int row_index = 0; row_index < parsed_rows.size(); row_index++) {
      List<String> row = parsed_rows.get(row_index);

      // For every element in a row representing different columns
      for (String element : row) {
        // If the element equals the target_string
        if (element.toLowerCase().contains(target_string.toLowerCase())) {
          rows_to_content.put(row_index, row);
          row_content.add(row);
        }
      }
    }
  }
}
