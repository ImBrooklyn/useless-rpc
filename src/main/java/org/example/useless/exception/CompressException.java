package org.example.useless.exception;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class CompressException extends RuntimeException {

    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }
}
