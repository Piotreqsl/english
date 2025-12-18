package org.example.lab5;

/**
 * Custom checked exception for CSV format errors (Task 4).
 */
public class CsvFormatException extends Exception {
    public CsvFormatException(String message) {
        super(message);
    }
    
    public CsvFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
