package com.ujjay.wanderly.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ujjay.wanderly.models.Trip;
import com.ujjay.wanderly.models.User;
import java.util.ArrayList;
import java.util.List;

public class TripDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "wanderly.db";
    private static final int DATABASE_VERSION = 3; // Increment version to 3 for schema change

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_TRAVEL_STYLE = "travel_style";
    private static final String COLUMN_BUDGET = "budget";
    private static final String COLUMN_USER_CREATED_AT = "user_created_at";

    // Trip table (updated)
    private static final String TABLE_TRIPS = "trips";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID_FK = "user_id"; // Foreign key
    private static final String COLUMN_DESTINATION = "destination";
    private static final String COLUMN_ITINERARY = "itinerary";
    private static final String COLUMN_CREATED_AT = "created_at";

    public TripDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table WITH PASSWORD COLUMN
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT," // ADD THIS LINE
                + COLUMN_TRAVEL_STYLE + " TEXT,"
                + COLUMN_BUDGET + " TEXT,"
                + COLUMN_USER_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create trips table with user foreign key
        String CREATE_TRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + COLUMN_DESTINATION + " TEXT,"
                + COLUMN_ITINERARY + " TEXT,"
                + COLUMN_CREATED_AT + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" + ")";
        db.execSQL(CREATE_TRIPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // USER METHODS - UPDATED FOR PASSWORD
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword()); // ADD PASSWORD
        values.put(COLUMN_TRAVEL_STYLE, user.getTravelStyle());
        values.put(COLUMN_BUDGET, user.getBudget());
        values.put(COLUMN_USER_CREATED_AT, user.getCreatedAt());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_TRAVEL_STYLE, COLUMN_BUDGET, COLUMN_USER_CREATED_AT},
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3)); // GET PASSWORD
            user.setTravelStyle(cursor.getString(4));
            user.setBudget(cursor.getString(5));
            user.setCreatedAt(cursor.getString(6));
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setUsername(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setPassword(cursor.getString(3)); // GET PASSWORD
                user.setTravelStyle(cursor.getString(4));
                user.setBudget(cursor.getString(5));
                user.setCreatedAt(cursor.getString(6));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword()); // UPDATE PASSWORD
        values.put(COLUMN_TRAVEL_STYLE, user.getTravelStyle());
        values.put(COLUMN_BUDGET, user.getBudget());

        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    // AUTHENTICATION METHODS - ADD THESE
    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_TRAVEL_STYLE, COLUMN_BUDGET, COLUMN_USER_CREATED_AT},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setTravelStyle(cursor.getString(4));
            user.setBudget(cursor.getString(5));
            user.setCreatedAt(cursor.getString(6));
            cursor.close();
            return user;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Add default users for testing
    public void addDefaultUsers() {
        if (getAllUsers().isEmpty()) {
            addUser(new User("admin", "admin@wanderly.com", "admin123"));
            addUser(new User("user", "user@wanderly.com", "user123"));
            addUser(new User("test", "test@wanderly.com", "test123"));
        }
    }

    // TRIP METHODS (updated for multi-user) - NO CHANGES NEEDED HERE
    public long addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, trip.getUserId());
        values.put(COLUMN_DESTINATION, trip.getDestination());
        values.put(COLUMN_ITINERARY, trip.getItinerary());
        values.put(COLUMN_CREATED_AT, trip.getCreatedAt());

        long id = db.insert(TABLE_TRIPS, null, values);
        db.close();
        return id;
    }

    public List<Trip> getUserTrips(int userId) {
        List<Trip> tripList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRIPS + " WHERE " + COLUMN_USER_ID_FK + " = " + userId + " ORDER BY " + COLUMN_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setId(cursor.getInt(0));
                trip.setUserId(cursor.getInt(1));
                trip.setDestination(cursor.getString(2));
                trip.setItinerary(cursor.getString(3));
                trip.setCreatedAt(cursor.getString(4));
                tripList.add(trip);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tripList;
    }

    public int getTripsCount(int userId) {
        String countQuery = "SELECT * FROM " + TABLE_TRIPS + " WHERE " + COLUMN_USER_ID_FK + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void clearUserTrips(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRIPS, COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public boolean tripExists(int userId, String destination) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TRIPS + " WHERE " + COLUMN_USER_ID_FK + " = ? AND " + COLUMN_DESTINATION + " = ?",
                new String[]{String.valueOf(userId), destination}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean deleteTrip(int tripId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRIPS, COLUMN_ID + " = ?", new String[]{String.valueOf(tripId)});
        db.close();
        return result > 0;
    }

    public Trip getTripById(int tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRIPS,
                new String[]{COLUMN_ID, COLUMN_USER_ID_FK, COLUMN_DESTINATION, COLUMN_ITINERARY, COLUMN_CREATED_AT},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(tripId)}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Trip trip = new Trip();
            trip.setId(cursor.getInt(0));
            trip.setUserId(cursor.getInt(1));
            trip.setDestination(cursor.getString(2));
            trip.setItinerary(cursor.getString(3));
            trip.setCreatedAt(cursor.getString(4));
            cursor.close();
            return trip;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}