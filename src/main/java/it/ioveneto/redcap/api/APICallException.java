package it.ioveneto.redcap.api;

public class APICallException extends RuntimeException{
    public APICallException(String message, Throwable cause) {
        super(message, cause);
    }

    public APICallException(String message) {
        super(message);
    }
}
