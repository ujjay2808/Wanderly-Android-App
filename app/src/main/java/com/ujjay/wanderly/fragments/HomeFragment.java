package com.ujjay.wanderly.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ujjay.wanderly.R;
import com.ujjay.wanderly.adapters.ChatAdapter;
import com.ujjay.wanderly.api.APICallback;
import com.ujjay.wanderly.api.GroqService;
import com.ujjay.wanderly.database.TripDatabaseHelper;
import com.ujjay.wanderly.models.APIResponse;
import com.ujjay.wanderly.models.Message;
import com.ujjay.wanderly.models.Trip;
import com.ujjay.wanderly.utils.NetworkUtils;
import com.ujjay.wanderly.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private GroqService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();

        apiService = new GroqService(requireContext());

        // Add welcome message
        addWelcomeMessage();

        return view;
    }

    private void initViews(View view) {
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        // Add long press listener for saving trips
        chatAdapter.setOnItemLongClickListener(new ChatAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position, Message message) {
                if (!message.isUser()) {
                    // This is an AI message - show save option
                    showSaveTripDialog(message.getText());
                    return true;
                }
                return false;
            }
        });

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
        messageEditText.setOnClickListener(v -> scrollToBottom());
    }

    private void sendMessage() {
        SessionManager session = new SessionManager(requireContext());
        if (!session.isLoggedIn()) {
            Toast.makeText(requireContext(), "Please login to use AI chat", Toast.LENGTH_SHORT).show();
            return;
        }
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to chat
        Message userMessage = new Message(messageText, true);
        chatAdapter.addMessage(userMessage);
        messageEditText.setText("");
        scrollToBottom();

        // Show loading message
        Message loadingMessage = new Message("Thinking...", false);
        chatAdapter.addMessage(loadingMessage);
        scrollToBottom();

        // Send to AI
        apiService.sendMessage(messageText, new APICallback() {
            @Override
            public void onSuccess(APIResponse response) {
                requireActivity().runOnUiThread(() -> {
                    // Remove loading message
                    messageList.remove(loadingMessage);
                    chatAdapter.notifyDataSetChanged();

                    // Add AI response
                    Message aiMessage = new Message(response.getContent(), false);
                    chatAdapter.addMessage(aiMessage);
                    scrollToBottom();
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    // Remove loading message
                    messageList.remove(loadingMessage);
                    chatAdapter.notifyDataSetChanged();

                    // Show error message
                    Message errorMessage = new Message("Error: " + error, false);
                    chatAdapter.addMessage(errorMessage);
                    scrollToBottom();
                    Toast.makeText(requireContext(), "API Error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void scrollToBottom() {
        chatRecyclerView.postDelayed(() -> {
            if (messageList.size() > 0) {
                chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
            }
        }, 100);
    }

    private void addWelcomeMessage() {
        String welcomeMessage = "Hello! I'm Wanderly, your AI travel assistant. " +
                "I can help you with:\n\n" +
                "• Destination recommendations\n" +
                "• Itinerary planning\n" +
                "• Packing lists\n" +
                "• Local tips and culture\n" +
                "• Budget estimation\n\n" +
                "Where would you like to go today?";

        Message welcome = new Message(welcomeMessage, false);
        chatAdapter.addMessage(welcome);
    }

    private void showSaveTripDialog(String itinerary) {
        // Get the original user message from the last user message
        String userMessage = "";
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message msg = messageList.get(i);
            if (msg.isUser()) {
                userMessage = msg.getText();
                break;
            }
        }

        final String finalUserMessage = userMessage;

        new AlertDialog.Builder(requireContext())
                .setTitle("Save This Trip?")
                .setMessage("Would you like to save this itinerary to your trips?")
                .setPositiveButton("Save", (dialog, which) -> {
                    saveTrip(itinerary, finalUserMessage);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // SIMPLIFIED: Just save the trip without dates/duration
    private void saveTrip(String itinerary, String userMessage) {
        SessionManager session = new SessionManager(requireContext());
        int currentUserId = session.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String destination = extractDestinationName(userMessage, itinerary);

        Trip trip = new Trip(currentUserId, destination, itinerary);

        TripDatabaseHelper dbHelper = new TripDatabaseHelper(requireContext());
        long id = dbHelper.addTrip(trip);

        if (id != -1) {
            Toast.makeText(requireContext(), "Trip to " + destination + " saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Failed to save trip", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractDestinationName(String userMessage, String aiResponse) {
        if (userMessage == null) userMessage = "";
        String message = userMessage.toLowerCase();

        // Simple destination mapping
        if (message.contains("paris") || message.contains("france")) return "Paris, France";
        if (message.contains("bali") || message.contains("indonesia")) return "Bali, Indonesia";
        if (message.contains("japan") || message.contains("tokyo")) return "Tokyo, Japan";
        if (message.contains("thailand") || message.contains("bangkok")) return "Thailand";
        if (message.contains("rome") || message.contains("italy")) return "Rome, Italy";
        if (message.contains("london") || message.contains("uk") || message.contains("united kingdom")) return "London, UK";
        if (message.contains("new york") || message.contains("nyc")) return "New York, USA";
        if (message.contains("dubai") || message.contains("uae")) return "Dubai, UAE";
        if (message.contains("sydney") || message.contains("australia")) return "Sydney, Australia";
        if (message.contains("mumbai") || message.contains("bombay")) return "Mumbai, India";

        // Try AI response
        if (aiResponse.toLowerCase().contains("paris")) return "Paris, France";
        if (aiResponse.toLowerCase().contains("bali")) return "Bali, Indonesia";
        if (aiResponse.toLowerCase().contains("tokyo")) return "Tokyo, Japan";
        if (aiResponse.toLowerCase().contains("london")) return "London, UK";
        if (aiResponse.toLowerCase().contains("new york")) return "New York, USA";
        if (aiResponse.toLowerCase().contains("mumbai")) return "Mumbai, India";

        // Fallback: use first meaningful word from user message
        String[] words = userMessage.split("\\s+");
        for (String word : words) {
            String cleanWord = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
            if (!cleanWord.isEmpty() &&
                    !cleanWord.equals("trip") &&
                    !cleanWord.equals("visit") &&
                    !cleanWord.equals("go") &&
                    !cleanWord.equals("to") &&
                    !cleanWord.equals("for") &&
                    !cleanWord.equals("days") &&
                    !cleanWord.equals("day") &&
                    !cleanWord.equals("plan") &&
                    !cleanWord.equals("itinerary")) {
                return word + " Trip";
            }
        }

        return "My Trip"; // Final fallback
    }
}