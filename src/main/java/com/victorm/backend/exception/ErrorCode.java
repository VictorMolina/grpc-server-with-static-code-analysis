package com.victorm.backend.exception;

public enum ErrorCode {

    READ_JSON_OBJECT(-4),
    WRITE_JSON_OBJECT(-3),
    SERVER_STARTUP(-2),
    UNKNOWN(-1),
    PROFILE_NOT_FOUND(1);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
