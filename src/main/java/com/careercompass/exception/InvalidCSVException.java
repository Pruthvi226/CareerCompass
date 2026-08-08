package com.careercompass.exception;

/**
 * Custom exception for invalid CSV data
 */
public class InvalidCSVException extends RuntimeException {

    public InvalidCSVException(String message) {
        super(message);
    }

    public InvalidCSVException(String message, Throwable cause) {
        super(message, cause);
    }
}
