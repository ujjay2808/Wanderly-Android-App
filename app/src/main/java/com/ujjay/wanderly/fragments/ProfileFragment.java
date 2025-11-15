package com.ujjay.wanderly.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.activities.LoginActivity;
import com.ujjay.wanderly.database.TripDatabaseHelper;
import com.ujjay.wanderly.models.User;
import com.ujjay.wanderly.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView userNameText, userBudgetText, travelStyleText, tripsCountText;
    private Button editProfileButton, clearHistoryButton, logoutButton;
    private SharedPreferences sharedPreferences;
    private TripDatabaseHelper tripDatabaseHelper;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupSession();
        setupSharedPreferences();
        loadUserProfile();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        userNameText = view.findViewById(R.id.user_name_text);
        userBudgetText = view.findViewById(R.id.user_budget_text);
        travelStyleText = view.findViewById(R.id.travel_style_text);
        tripsCountText = view.findViewById(R.id.trips_count_text);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        clearHistoryButton = view.findViewById(R.id.clear_history_button);
        logoutButton = view.findViewById(R.id.logout_button);

        tripDatabaseHelper = new TripDatabaseHelper(requireContext());
        session = new SessionManager(requireContext());
    }

    private void setupSession() {
        session = new SessionManager(requireContext());
    }

    private void setupSharedPreferences() {
        sharedPreferences = requireContext().getSharedPreferences("WanderlyPrefs", getContext().MODE_PRIVATE);
    }

    private void loadUserProfile() {
        int currentUserId = session.getUserId();

        if (currentUserId == -1) {
            // User not logged in
            userNameText.setText("Guest");
            userBudgetText.setText("Not set");
            travelStyleText.setText("Not set");
            tripsCountText.setText("0");
            logoutButton.setText("Login");
            return;
        }

        // Load user data from database
        User currentUser = tripDatabaseHelper.getUser(currentUserId);

        if (currentUser != null) {
            userNameText.setText(currentUser.getUsername());
            userBudgetText.setText(currentUser.getBudget());
            travelStyleText.setText(currentUser.getTravelStyle());

            // Load trips count for current user
            int tripsCount = tripDatabaseHelper.getTripsCount(currentUserId);
            tripsCountText.setText(String.valueOf(tripsCount));
        }
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
        clearHistoryButton.setOnClickListener(v -> showClearHistoryConfirmation());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void handleLogout() {
        if (session.isLoggedIn()) {
            // Show logout confirmation
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        session.logoutUser();
                        redirectToLogin();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // User is not logged in, redirect to login
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    // ADD THESE MISSING METHODS:

    private void showEditProfileDialog() {
        int currentUserId = session.getUserId();
        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "Please login to edit profile", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Your Travel Profile");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.name_input);
        EditText budgetInput = dialogView.findViewById(R.id.budget_input);
        RadioGroup travelStyleGroup = dialogView.findViewById(R.id.travel_style_group);

        // Get current user data
        User currentUser = tripDatabaseHelper.getUser(currentUserId);

        // Pre-fill current values
        if (currentUser != null) {
            nameInput.setText(currentUser.getUsername());
            // Remove $ symbol if present
            String budget = currentUser.getBudget().replace("$", "");
            budgetInput.setText(budget.equals("Not set") ? "" : budget);

            String currentStyle = currentUser.getTravelStyle();
            switch (currentStyle) {
                case "Budget Backpacker":
                    travelStyleGroup.check(R.id.style_budget);
                    break;
                case "Luxury Traveler":
                    travelStyleGroup.check(R.id.style_luxury);
                    break;
                case "Adventure Seeker":
                    travelStyleGroup.check(R.id.style_adventure);
                    break;
                case "Cultural Explorer":
                    travelStyleGroup.check(R.id.style_cultural);
                    break;
            }
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String budget = budgetInput.getText().toString().trim();
            String travelStyle = getSelectedTravelStyle(travelStyleGroup);

            if (name.isEmpty()) {
                name = "Traveler";
            }

            if (budget.isEmpty()) {
                budget = "Not set";
            } else {
                budget = "$" + budget;
            }

            if (travelStyle.isEmpty()) {
                travelStyle = "Not set";
            }

            // Save to database
            User user = tripDatabaseHelper.getUser(currentUserId);
            if (user != null) {
                user.setUsername(name);
                user.setBudget(budget);
                user.setTravelStyle(travelStyle);
                tripDatabaseHelper.updateUser(user);
            }

            // Update UI
            loadUserProfile();
            Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private String getSelectedTravelStyle(RadioGroup travelStyleGroup) {
        int selectedId = travelStyleGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.style_budget) {
            return "Budget Backpacker";
        } else if (selectedId == R.id.style_luxury) {
            return "Luxury Traveler";
        } else if (selectedId == R.id.style_adventure) {
            return "Adventure Seeker";
        } else if (selectedId == R.id.style_cultural) {
            return "Cultural Explorer";
        }
        return "";
    }

    private void showClearHistoryConfirmation() {
        int currentUserId = session.getUserId();
        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "Please login to clear trips", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Clear Trip History")
                .setMessage("Are you sure you want to delete all your saved trips? This action cannot be undone.")
                .setPositiveButton("Delete All", (dialog, which) -> {
                    tripDatabaseHelper.clearUserTrips(currentUserId);
                    loadUserProfile(); // Refresh trips count
                    Toast.makeText(requireContext(), "All trips deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh data when fragment becomes visible
    }
}