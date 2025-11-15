package com.ujjay.wanderly.models;

public class Message {
    private String text;
    private boolean isUser;
    private long timestamp;

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String text, boolean isUser, long timestamp) {
        this.text = text;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}