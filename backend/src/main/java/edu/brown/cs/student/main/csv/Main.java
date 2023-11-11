package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.csv.searcher.Search;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) throws IOException, FactoryFailureException {
    new Main(args).run();
  }

  private Main(String[] args) {}

  /** Begins program and receives user input. */
  private void run() {
    boolean hasHeader = false;
    Scanner myObj = new Scanner(System.in);
    System.out.println("Enter filename: ");
    String filename = myObj.nextLine();
    System.out.println("Does your file have headers? (y/n)");
    String header = myObj.nextLine().toLowerCase();
    if (header.equals("y")) {
      hasHeader = true;
    }

    System.out.println("Enter keyword: ");
    String target = myObj.nextLine().toLowerCase();
    if (target.isEmpty()) {
      System.err.println("Keyword needed. Please try again.");
    }
    System.out.println(
        "Enter column identifier in the form of a number or column name (Optional): ");
    String identifier = myObj.nextLine().toLowerCase();
    try {
      FileReader fileReader = new FileReader(filename);
      Search mySearch = new Search(fileReader, target, identifier, hasHeader);
      mySearch.beginSearch();

    } catch (IOException e) {
      System.err.println("Error reading file, file may not exist: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("An unexpected error occurred, please try again: " + e.getMessage());
    }
  }
}
