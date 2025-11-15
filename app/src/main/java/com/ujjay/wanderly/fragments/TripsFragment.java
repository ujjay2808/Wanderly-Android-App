package com.ujjay.wanderly.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.activities.TripDetailActivity;
import com.ujjay.wanderly.adapters.TripAdapter;
import com.ujjay.wanderly.database.TripDatabaseHelper;
import com.ujjay.wanderly.models.Trip;
import com.ujjay.wanderly.utils.SessionManager;

import java.util.List;

public class TripsFragment extends Fragment {
    private RecyclerView tripsRecyclerView;
    private TextView emptyStateText;
    private TripAdapter tripAdapter;
    private TripDatabaseHelper databaseHelper;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        initViews(view);
        setupRecyclerView();
        loadUserTrips();

        return view;
    }

    private void initViews(View view) {
        tripsRecyclerView = view.findViewById(R.id.trips_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        databaseHelper = new TripDatabaseHelper(requireContext());
        session = new SessionManager(requireContext());
    }

    private void setupRecyclerView() {
        tripAdapter = new TripAdapter(null, new TripAdapter.OnTripClickListener() {
            @Override
            public void onTripClick(Trip trip) {
                // Open trip detail activity
                openTripDetail(trip);
            }

            @Override
            public void onTripLongClick(Trip trip) {
                // Show delete option
                showDeleteTripDialog(trip);
            }
        });

        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripsRecyclerView.setAdapter(tripAdapter);
    }

    private void loadUserTrips() {
        int currentUserId = session.getUserId();

        if (currentUserId == -1) {
            // User not logged in
            emptyStateText.setText("Please login to view your trips");
            emptyStateText.setVisibility(View.VISIBLE);
            tripsRecyclerView.setVisibility(View.GONE);
            return;
        }

        List<Trip> userTrips = databaseHelper.getUserTrips(currentUserId);
        tripAdapter.setTrips(userTrips);

        // Show empty state if no trips
        if (userTrips.isEmpty()) {
            emptyStateText.setText("No trips saved yet!\n\nStart a conversation with Wanderly AI to plan your first trip.");
            emptyStateText.setVisibility(View.VISIBLE);
            tripsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            tripsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void openTripDetail(Trip trip) {
        try {
            Intent intent = new Intent(requireContext(), TripDetailActivity.class);
            intent.putExtra("trip_data", trip); // This should work now that Trip is Serializable
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error opening trip details", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteTripDialog(Trip trip) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Trip?")
                .setMessage("Are you sure you want to delete this trip to " + trip.getDestination() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteTrip(trip.getId());
                    if (deleted) {
                        loadUserTrips(); // Refresh the list
                        Toast.makeText(requireContext(), "Trip deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete trip", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserTrips(); // Refresh when returning to fragment
    }
}