package com.kene.esdiactest.config;

import org.springframework.http.HttpStatus;

public class ErrorResponse extends RuntimeException {

    private final ApiResponse<?> apiResponse;

    public ErrorResponse(HttpStatus status, String message){
        super(message);
         apiResponse = new ApiResponse<>(status.value(),message);
    }

    public ErrorResponse(int status, String message){
        super(message);
        apiResponse = new ApiResponse<>(status,message);
    }

    public ErrorResponse(ApiResponse<?> apiResponse) {
        super(apiResponse.getMessage());
        this.apiResponse = apiResponse;
    }

    public ApiResponse<?> getApiResponse() {
        return apiResponse;
    }
}
