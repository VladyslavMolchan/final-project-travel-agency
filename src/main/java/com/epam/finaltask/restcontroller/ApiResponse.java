package com.epam.finaltask.restcontroller;

public class ApiResponse<T> {

    private String statusCode;
    private String statusMessage;
    private T results;

    public ApiResponse() {
    }

    public ApiResponse(String statusCode, String statusMessage, T results) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.results = results;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
