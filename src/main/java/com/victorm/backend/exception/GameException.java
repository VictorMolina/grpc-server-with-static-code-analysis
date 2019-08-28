package com.victorm.backend.exception;

public class GameException extends RuntimeException {
    private static final long serialVersionUID = -2264261510998222216L;

    private final ErrorCode errorCode;

    public GameException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GameException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
