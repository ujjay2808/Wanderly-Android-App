package com.ujjay.wanderly.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.database.TripDatabaseHelper;
import com.ujjay.wanderly.models.User;
import com.ujjay.wanderly.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword, editTextEmail;
    private Button buttonLogin;
    private TextView textSwitchToSignup;
    private boolean isLoginMode = true;

    private TripDatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startMainActivity();
            return;
        }

        initViews();
        dbHelper = new TripDatabaseHelper(this);

        setupClickListeners();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.edit_text_username);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextEmail = findViewById(R.id.edit_text_email);
        buttonLogin = findViewById(R.id.button_login);
        textSwitchToSignup = findViewById(R.id.text_switch_mode);

        updateUIForMode();
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> handleAuth());

        textSwitchToSignup.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateUIForMode();
        });
    }

    private void updateUIForMode() {
        if (isLoginMode) {
            buttonLogin.setText("Login");
            textSwitchToSignup.setText("Don't have an account? Sign up");
            editTextEmail.setVisibility(View.GONE);
        } else {
            buttonLogin.setText("Sign Up");
            textSwitchToSignup.setText("Already have an account? Login");
            editTextEmail.setVisibility(View.VISIBLE);
        }
    }

    private void handleAuth() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLoginMode && email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLoginMode) {
            loginUser(username, password);
        } else {
            signupUser(username, email, password);
        }
    }

    private void loginUser(String username, String password) {
        User user = dbHelper.authenticateUser(username, password);
        if (user != null) {
            session.createLoginSession(user.getId(), user.getUsername());
            startMainActivity();
            Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupUser(String username, String email, String password) {
        if (dbHelper.usernameExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, email, password);
        long userId = dbHelper.addUser(user);

        if (userId != -1) {
            session.createLoginSession((int) userId, username);
            startMainActivity();
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}