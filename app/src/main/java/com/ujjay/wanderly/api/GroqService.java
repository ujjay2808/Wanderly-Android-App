package com.ujjay.wanderly.api;

import android.content.Context;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ujjay.wanderly.models.APIResponse;
import com.ujjay.wanderly.utils.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroqService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;
    private final Context context;

    public GroqService(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void sendMessage(String userMessage, APICallback callback) {
        new GroqCallTask(callback).execute(userMessage);
    }

    // NEW METHOD: Extract duration and create enhanced prompt
    private String createEnhancedPrompt(String userMessage) {
        int duration = extractDurationFromMessage(userMessage);
        String[] dates = getTripDates(userMessage, duration);

        String startDate = dates[0];
        String endDate = dates[1];

        // Format dates for display (e.g., "Nov 15, 2025")
        String displayStartDate = formatDateForDisplay(startDate);
        String displayEndDate = formatDateForDisplay(endDate);

        // Create enhanced prompt with exact dates
        return "Create a travel itinerary for: " + userMessage +
                "\n\nIMPORTANT: Use these EXACT dates: " + displayStartDate + " to " + displayEndDate +
                " (" + duration + " days total)" +
                "\n\nPlease structure the itinerary using these specific dates. Do NOT invent different dates." +
                "\n\nFormat your response with:" +
                "\n- Day-by-day itinerary using the actual dates provided" +
                "\n- Morning, afternoon, and evening activities" +
                "\n- Restaurant recommendations" +
                "\n- Transportation tips" +
                "\n- Budget estimates" +
                "\n- Packing suggestions" +
                "\n- Cultural tips";
    }

    // Duration extraction (same as HomeFragment)
    private int extractDurationFromMessage(String userMessage) {
        if (userMessage == null || userMessage.isEmpty()) {
            return 3;
        }

        String message = userMessage.toLowerCase();

        Pattern dayPattern = Pattern.compile("(\\d+)\\s*day");
        Matcher matcher = dayPattern.matcher(message);
        if (matcher.find()) {
            try {
                int days = Integer.parseInt(matcher.group(1));
                return Math.max(1, Math.min(days, 30));
            } catch (NumberFormatException e) {
                // Continue
            }
        }

        Pattern datePattern = Pattern.compile("(\\d+)\\s*to\\s*(\\d+)");
        Matcher dateMatcher = datePattern.matcher(message);
        if (dateMatcher.find()) {
            try {
                int start = Integer.parseInt(dateMatcher.group(1));
                int end = Integer.parseInt(dateMatcher.group(2));
                int days = Math.abs(end - start) + 1;
                return Math.max(1, Math.min(days, 30));
            } catch (NumberFormatException e) {
                // Continue
            }
        }

        if (message.contains("weekend")) return 2;
        if (message.contains("week") || message.contains("7 day")) return 7;
        if (message.contains("month")) return 30;

        return 3;
    }

    private String[] getTripDates(String userMessage, int duration) {
        String startDate = getCurrentDate();
        String endDate = calculateEndDate(startDate, duration);
        return new String[]{startDate, endDate};
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String calculateEndDate(String startDate, int duration) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, duration - 1);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return startDate;
        }
    }

    private String formatDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            return date;
        }
    }

    private class GroqCallTask extends AsyncTask<String, Void, APIResponse> {
        private final APICallback callback;

        public GroqCallTask(APICallback callback) {
            this.callback = callback;
        }

        @Override
        protected APIResponse doInBackground(String... messages) {
            String userMessage = messages[0];

            try {
                // Create enhanced prompt with dates
                String enhancedPrompt = createEnhancedPrompt(userMessage);

                // Create request body for Groq
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", Constants._GROQ_MODEL);
                requestBody.addProperty("temperature", 0.7);
                requestBody.addProperty("max_tokens", 1024);

                // Create messages array
                JsonArray messagesArray = new JsonArray();

                // System message - Travel assistant persona
                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", "You are Wanderly, a helpful AI travel assistant. Provide detailed travel recommendations, itinerary planning, packing lists, and local tips. Format responses with clear headings using **bold** and bullet points. Keep responses comprehensive and useful for travelers. When creating itineraries, make them practical and include specific recommendations. Always use the exact dates provided by the user - do not invent different dates.");
                messagesArray.add(systemMessage);

                // User message with enhanced prompt
                JsonObject userMessageObj = new JsonObject();
                userMessageObj.addProperty("role", "user");
                userMessageObj.addProperty("content", enhancedPrompt);
                messagesArray.add(userMessageObj);

                requestBody.add("messages", messagesArray);
                requestBody.addProperty("stream", false);

                RequestBody body = RequestBody.create(gson.toJson(requestBody), JSON);

                // Build request with your REAL API key
                Request request = new Request.Builder()
                        .url(Constants._GROQ_API_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + Constants._GROQ_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    // Parse the Groq response
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                        String content = jsonResponse.getAsJsonArray("choices")
                                .get(0)
                                .getAsJsonObject()
                                .get("message")
                                .getAsJsonObject()
                                .get("content")
                                .getAsString();

                        return new APIResponse(content, true);
                    } else {
                        return new APIResponse("No response generated. Please try again.");
                    }
                } else {
                    return new APIResponse("API Error: " + response.code() + " - " + responseBody);
                }

            } catch (IOException e) {
                return new APIResponse("Network error: " + e.getMessage());
            } catch (Exception e) {
                return new APIResponse("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(APIResponse result) {
            if (result.isSuccess()) {
                callback.onSuccess(result);
            } else {
                callback.onError(result.getError());
            }
        }
    }
}