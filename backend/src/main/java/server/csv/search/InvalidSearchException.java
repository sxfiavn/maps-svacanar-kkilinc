package server.csv.search;

import java.util.ArrayList;
import java.util.List;

public class InvalidSearchException extends Exception {

    /**
     * Constructs a new InvalidSearchException. Allows for a specific error message and has the row
     * that triggered the exception
     *
     * @param message Error message explaining the cause of the failure.
     * @param row The row of data that triggered the exception.
     */
    public InvalidSearchException(String message) {
        super(message);
    }
}
