package com.kene.esdiactest.config;

import java.util.HashMap;

public class ApiResponse<T> {
    private String statusMsg;
    private String statusCode;
    private String responseCode;
    private Integer code;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String statusCode, String statusMsg, T data) {
        this.statusMsg = statusMsg;
        this.statusCode = statusCode;
        this.data = data;
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public ApiResponse<T> setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
        return this;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public ApiResponse<T> setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public ApiResponse<T> setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public ApiResponse<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
