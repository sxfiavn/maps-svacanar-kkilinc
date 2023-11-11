package edu.brown.cs.student.main.csv.parser;

import edu.brown.cs.student.main.csv.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** The CSVParser class of this project. This is where the parsing from csv to structure occurs. */
public class CsvParser<T> implements Parser {

  private Reader reader;
  private CreatorFromRow<T> creatorFromRow;

  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * The CsvParser constructor.
   *
   * @param reader - a reader object
   * @param creatorFromRow - A creator from row object
   */
  public CsvParser(Reader reader, CreatorFromRow<T> creatorFromRow) {
    this.reader = reader;
    this.creatorFromRow = creatorFromRow;
  }

  /**
   * The parse method takes the data and turns them into T objects in a list.
   *
   * @return - a list of objects of type T
   * @throws IOException - when an I/O error occurs during the parsing
   * @throws FactoryFailureException - when there is a failure in creating objects of type T
   */
  public ArrayList<T> parse() throws IOException, FactoryFailureException {
    ArrayList<T> data = new ArrayList<>();
    BufferedReader br = new BufferedReader(this.reader);
    String line;
    while ((line = br.readLine()) != null) {
      String[] valuesArray = regexSplitCSVRow.split(line);
      List<String> values = new ArrayList<>();
      for (String value : valuesArray) {
        // get rid of empty space
        value = value.trim();
        // get rid of extra quotes
        if (value.startsWith("\"") && value.endsWith("\"")) {
          value = value.substring(1, value.length() - 1);
        }
        values.add(value.toLowerCase());
      }
      data.add(this.creatorFromRow.create(values));
    }
    br.close();
    return data;
  }
}
