package com.zhaodong.fileutils.exceptions;


import com.zhaodong.fileutils.base.Status;

public class ServiceException extends RuntimeException {
    private int code;
    private Object o;
    public ServiceException(Status status) {
        super(status.getMessage());
        this.code=status.getCode();
    }

    public ServiceException(Status status,Object o) {
        super(status.getMessage());
        this.code=status.getCode();
        this.o=o;
    }

    public int getCode() {
        return code;
    }

    public Object getO() {
        return o;
    }
}
