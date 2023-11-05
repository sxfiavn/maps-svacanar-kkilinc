package server.csv.parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class parser<T> {

  /**
   * @param reader Generic reader object containing CSV.
   * @param createMethod Method indicating what type of object each row of the CSV is going to be
   *     converted to and how to do it.
   * @return List of rows of type object T.
   * @throws FactoryFailureException
   * @throws IOException
   */
  public List<T> Parser(Reader reader, CreatorFromRow<T> createMethod)
      throws FactoryFailureException, IOException {

    // Regex to parse CSV
    final Pattern regexSplitCSVRow =
        Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

    // Wrap the provided Reader in a BufferedReader for line-by-line reading
    BufferedReader bufferedReader = new BufferedReader(reader);
    String row; // initialize row as a string variable
    String[] row_list; // initialize string array
    ArrayList<T> return_list = new ArrayList<T>(); // initialize what will be the output list
    T creator; // initialize varible that will be reused for each row

    Integer prev_row_length = null;

    // Parse each row in the reader
    while ((row = bufferedReader.readLine()) != null) { // while there are still lines to read
      row_list = regexSplitCSVRow.split(row); // parse the individual row with the regex

      if (prev_row_length == null) {
        prev_row_length = row_list.length;
      } else if (prev_row_length != row_list.length) {
        throw new RuntimeException("Inconsistent number of columns per row in the CSV file.");
      }

      try {
        creator = createMethod.create(row_list);
        return_list.add(creator);
      } catch (FactoryFailureException e) { // to catch the error when handled in the createMethod
        throw e; // handle in Main
      }
    }

    bufferedReader.close();
    return return_list;
  }
}
