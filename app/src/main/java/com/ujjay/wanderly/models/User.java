package com.ujjay.wanderly.models;

public class User {
    private int id;
    private String username;
    private String email;
    private String password; // Add this field
    private String travelStyle;
    private String budget;
    private String createdAt;

    public User() {}

    public User(String username, String email, String password) { // Update constructor
        this.username = username;
        this.email = email;
        this.password = password; // Initialize password
        this.travelStyle = "Not set";
        this.budget = "Not set";
        this.createdAt = new java.util.Date().toString();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; } // Add getter
    public void setPassword(String password) { this.password = password; } // Add setter

    public String getTravelStyle() { return travelStyle; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}