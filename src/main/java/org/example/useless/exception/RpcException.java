package org.example.useless.exception;

/**
 * @author ImBrooklyn
 * @since 19/08/2023
 */
public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
