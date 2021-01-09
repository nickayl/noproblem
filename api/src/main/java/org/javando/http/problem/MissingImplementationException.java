package org.javando.http.problem;

public class MissingImplementationException extends RuntimeException {
    public MissingImplementationException(String message) {
        super(message);
    }

    public MissingImplementationException(String message, Throwable cause) {
        super(message, cause);
    }
}