package com.ujjay.wanderly.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.models.Trip;

public class TripDetailActivity extends AppCompatActivity {

    private TextView destinationText;
    private TextView itineraryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        initViews();
        loadTripData();
    }

    private void initViews() {
        destinationText = findViewById(R.id.detail_destination_text);
        itineraryText = findViewById(R.id.detail_itinerary_text);
    }

    private void loadTripData() {
        // Get trip data from intent
        Trip trip = (Trip) getIntent().getSerializableExtra("trip_data");

        if (trip != null) {
            destinationText.setText(trip.getDestination());
            itineraryText.setText(trip.getItinerary());
        }
    }
}