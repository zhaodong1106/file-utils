package com.zhaodong.fileutils.base;

public enum Status {
    INVALID_PARAMETER(400,"invalid paramter"),
    ACCESS_DENY(401,"access deny"),
    INNER_SERVER_ERROR(500,"inner server error");
    private int code;
    private String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
