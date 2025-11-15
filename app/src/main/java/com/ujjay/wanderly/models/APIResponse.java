package com.ujjay.wanderly.models;

public class APIResponse {
    private String content;
    private boolean success;
    private String error;

    public APIResponse(String content, boolean success) {
        this.content = content;
        this.success = success;
    }

    public APIResponse(String error) {
        this.success = false;
        this.error = error;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}