package com.zhaodong.fileutils.base;

public class ResponseMessage {
    private int code;
    private String message;
    private Object data;

    public ResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseMessage(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static  ResponseMessage ofSuccess(Object data){
        return new ResponseMessage(200,"OK",data);
    }

    public ResponseMessage() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
