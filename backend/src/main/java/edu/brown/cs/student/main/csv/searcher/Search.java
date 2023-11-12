package edu.brown.cs.student.main.csv.searcher;

import edu.brown.cs.student.main.csv.FactoryFailureException;
import edu.brown.cs.student.main.csv.creators.CreateArrayList;
import edu.brown.cs.student.main.csv.creators.CreatorFromRow;
import edu.brown.cs.student.main.csv.parser.CsvParser;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/** The Search class of our project. This is where searching for the keyword occurs. */
public class Search {
  private final Reader reader;
  private String target;
  private String identifier;
  private boolean hasHeader;
  private List<ArrayList<String>> parsedData;

  /**
   * The constructor of the search class.
   *
   * @param reader - a reader object
   * @param target - a string keyword
   * @param identifier - a column number/word in the form of a string
   * @param hasHeader - a boolean of whether the file has a header
   */
  public Search(Reader reader, String target, String identifier, boolean hasHeader) {
    this.reader = reader;
    this.target = target;
    this.identifier = identifier;
    this.hasHeader = hasHeader;
  }

  /** The beginSearch method parses the data and decides how to search the file. */
  public ArrayList<ArrayList<String>> beginSearch() {
    return beginSearch(null);
  }

  /**
   * The beginSearch method parses the data and decides how to search the file.
   *
   * @param inputData - The data to conduct the search on.
   * @return A nested array of rows that had the target.
   */
  public ArrayList<ArrayList<String>> beginSearch(ArrayList<ArrayList<String>> inputData) {
    ArrayList<ArrayList<String>> rowResults = new ArrayList<>();

    try {
      if (inputData == null) {
        CreatorFromRow<ArrayList<String>> creatorFromRow = new CreateArrayList();
        CsvParser<ArrayList<String>> myParser = new CsvParser<>(reader, creatorFromRow);
        this.parsedData = myParser.parse();
      } else {

        this.parsedData = inputData;
      }

      ArrayList<String> header = this.parsedData.get(0);
      if (this.isNumeric(this.identifier)) {
        int columnNumber =
            Integer.parseInt(this.identifier)
                - 1; // I was confused about this, should note that column identifiers should be
        // entered starting from 1 not 0.
        this.searchRowsWithFromColumnNumber(rowResults, columnNumber);
      } else if (this.hasHeader && header.contains(this.identifier)) {
        int columnNumber = header.indexOf(this.identifier);
        this.searchRowsWithFromColumnNumber(rowResults, columnNumber);
      } else {
        this.searchAllRows(rowResults);
      }
    } catch (FactoryFailureException e) {
      System.err.println("Factory failure, please try again: " + e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return rowResults;
  }

  /** The isNumeric method checks to see if the identifier is a number. */
  private boolean isNumeric(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  /**
   * The searchRowsWithFromColumnNumber method checks only a column from the rows.
   *
   * @param columnNumber - a number representing the column number
   */
  private void searchRowsWithFromColumnNumber(
      ArrayList<ArrayList<String>> rowResults, int columnNumber) {
    for (ArrayList<String> row : this.parsedData) {
      if (columnNumber >= 0 && columnNumber < row.size()) {
        if (row.get(columnNumber).equals(this.target)) {
          System.out.println("Keyword found in row: " + row);
          rowResults.add(row);
        }
      }
      if (columnNumber > row.size() || columnNumber < 0) {
        System.err.println("Column is out of bounds, please try again.");
      }
    }
  }

  /** The searchAllRows method searches all columns and rows. */
  private void searchAllRows(ArrayList<ArrayList<String>> rowResults) {
    for (ArrayList<String> row : this.parsedData) {
      if (row.contains(this.target)) {
        System.out.println("Keyword found in row: " + row);
        rowResults.add(row);
      }
    }
  }
}
