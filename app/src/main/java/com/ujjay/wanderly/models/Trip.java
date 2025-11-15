package com.ujjay.wanderly.models;
import java.io.Serializable;

public class Trip implements Serializable  {
    private int id;
    private int userId; // Add user ID to associate trip with user
    private String destination;
    private String itinerary;
    private String createdAt;

    public Trip() {}

    public Trip(int userId, String destination, String itinerary) {
        this.userId = userId;
        this.destination = destination;
        this.itinerary = itinerary;
        this.createdAt = new java.util.Date().toString();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getItinerary() { return itinerary; }
    public void setItinerary(String itinerary) { this.itinerary = itinerary; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}