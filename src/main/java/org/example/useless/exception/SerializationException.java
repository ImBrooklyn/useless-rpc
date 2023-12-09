package org.example.useless.exception;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class SerializationException extends RuntimeException {

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
