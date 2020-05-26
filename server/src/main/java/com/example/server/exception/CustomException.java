package com.example.server.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {
    private HttpStatus httpStatus;

    public CustomException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
