# Wanderly - AI Travel Companion 🧳✈️

An intelligent Android application that revolutionizes trip planning with AI-powered itinerary suggestions using the Groq API. Plan your perfect vacation with personalized recommendations, budget management, and day-by-day schedules.

![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)
![API](https://img.shields.io/badge/API-21%2B-yellow.svg)

## ✨ Features

- 🔐 **Secure Authentication** - User login and signup with encrypted password storage
- 🗺️ **Smart Trip Management** - Create, view, edit, and organize travel itineraries
- 🤖 **AI-Powered Planning** - Get intelligent travel suggestions using Groq's LLM API
- 💾 **Offline Support** - SQLite database for accessing trips without internet
- 🎨 **Material Design UI** - Clean, intuitive interface following Material Design principles
- 📱 **Fragment Navigation** - Seamless navigation between Home, Trips, and Profile sections
- 💬 **Interactive Chat** - Real-time conversation with AI for personalized travel advice
- 📊 **Budget Tracking** - Manage travel expenses and get budget-optimized recommendations

## 📸 Screenshots

| Splash Screen | Login Screen | Main Dashboard |
|:-------------:|:------------:|:--------------:|
| ![Splash](https://via.placeholder.com/200x400/2196F3/FFFFFF?text=Splash) | ![Login](https://via.placeholder.com/200x400/4CAF50/FFFFFF?text=Login) | ![Dashboard](https://via.placeholder.com/200x400/FF9800/FFFFFF?text=Dashboard) |

| Trip Planning | Profile | Itinerary Details |
|:-------------:|:-------:|:-----------------:|
| ![Trips](https://via.placeholder.com/200x400/9C27B0/FFFFFF?text=Trips) | ![Profile](https://via.placeholder.com/200x400/F44336/FFFFFF?text=Profile) | ![Details](https://via.placeholder.com/200x400/607D8B/FFFFFF?text=Details) |

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java |
| **Framework** | Android SDK (API 21+) |
| **Database** | SQLite |
| **AI API** | Groq API (LLaMA 3, Mixtral) |
| **HTTP Client** | OkHttp3 |
| **JSON Parser** | Gson |
| **Architecture** | MVC with Fragments |
| **UI Components** | Material Design |

## 📁 Project Structure

```
app/src/main/java/com/ujjay/wanderly/
├── activities/
│   ├── SplashActivity.java          # App launch screen
│   ├── LoginActivity.java           # User authentication
│   ├── MainActivity.java            # Main container with fragments
│   └── TripDetailActivity.java      # Detailed trip view
├── fragments/
│   ├── HomeFragment.java            # AI chat interface
│   ├── TripsFragment.java           # Saved trips list
│   └── ProfileFragment.java         # User profile & settings
├── models/
│   ├── User.java                    # User data model
│   ├── Trip.java                    # Trip data model
│   ├── Message.java                 # Chat message model
│   └── APIResponse.java             # API response wrapper
├── database/
│   └── TripDatabaseHelper.java      # SQLite database handler
├── api/
│   └── GroqService.java             # Groq API integration
├── utils/
│   ├── Constants.java               # App-wide constants
│   ├── SessionManager.java          # User session management
│   └── NetworkUtils.java            # Network connectivity helpers
└── adapters/
    ├── TripAdapter.java             # RecyclerView adapter for trips
    └── ChatAdapter.java             # RecyclerView adapter for messages
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Arctic Fox (2020.3.1) or later
- **Android SDK** API Level 21 (Lollipop) or higher
- **Java Development Kit** 8 or higher
- **Groq API Key** (free tier available)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/ujjay2808/Wanderly-Android-App.git
   cd Wanderly-Android-App
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select `File > Open`
   - Navigate to the cloned directory and click `OK`

3. **Configure API Key**
   - Visit [GroqCloud Console](https://console.groq.com/)
   - Sign up and generate your API key
   - Open `app/src/main/java/com/ujjay/wanderly/utils/Constants.java`
   - Replace the placeholder with your API key:
   ```java
   public static final String GROQ_API_KEY = "your_actual_groq_api_key_here";
   ```

4. **Sync Gradle**
   - Click `File > Sync Project with Gradle Files`
   - Wait for dependencies to download

5. **Build and Run**
   - Connect an Android device (enable USB debugging) or start an emulator
   - Click the `Run` button (green play icon) or press `Shift + F10`

### Default Test Accounts

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | Administrator |
| `user` | `user123` | Regular User |
| `test` | `test123` | Testing Account |

You can also create new accounts using the signup feature.

## 📖 Usage Guide

### Planning Your First Trip

1. **Launch the App** - Greeted by an elegant splash screen
2. **Login/Signup** - Use test credentials or create a new account
3. **Navigate to Home** - Access the AI chat interface
4. **Start Chatting** - Type your travel request, for example:
   ```
   "Plan a 5-day trip to Tokyo, Japan with a $2000 budget"
   ```
5. **Get AI Recommendations** - Receive a detailed itinerary including:
   - Day-by-day activity schedule
   - Restaurant and dining suggestions
   - Transportation options and tips
   - Budget breakdown per day
   - Packing list recommendations
   - Local tips and cultural insights

6. **Save Your Trip** - Itineraries are automatically saved to the Trips section
7. **View & Edit** - Access saved trips anytime from the Trips tab
8. **Update Profile** - Set travel preferences and budget in the Profile section

## 🔧 API Configuration

### Groq API Setup

Wanderly uses Groq's lightning-fast LLM inference API for generating travel recommendations.

**Available Models:**
- `llama3-8b-8192` (Default - Fast & Efficient)
- `llama3-70b-8192` (More Detailed Responses)
- `mixtral-8x7b-32768` (Extended Context)

**API Endpoint:**
```
POST https://api.groq.com/openai/v1/chat/completions
```

**Rate Limits:**
- Free Tier: 30 requests per minute
- See [Groq Documentation](https://console.groq.com/docs) for details

## 💾 Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    travel_style TEXT,
    budget REAL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Trips Table
```sql
CREATE TABLE trips (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    destination TEXT NOT NULL,
    itinerary TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines

- Follow standard Java naming conventions
- Write clear, descriptive commit messages
- Add comments for complex logic
- Test on multiple device sizes and API levels
- Ensure backward compatibility with Android API 21+
- Update documentation for new features

## 🐛 Troubleshooting

### Common Issues

**1. API Key Errors**
```
Solution: 
- Verify API key is correctly set in Constants.java
- Check internet connectivity
- Ensure API key has proper permissions on GroqCloud
```

**2. Build Failures**
```
Solution:
- Clean project: Build > Clean Project
- Rebuild: Build > Rebuild Project
- Invalidate caches: File > Invalidate Caches > Invalidate and Restart
```

**3. Database Issues**
```
Solution:
- Clear app data: Settings > Apps > Wanderly > Clear Data
- Check database version compatibility
- Verify SQL schema syntax
```

**4. Network Errors**
```
Solution:
- Check internet permission in AndroidManifest.xml
- Verify network connectivity
- Test API endpoint with Postman
```

### Debug Logs

Enable verbose logging in `Constants.java`:
```java
public static final boolean DEBUG_MODE = true;
```

View logs in Android Studio's Logcat with filter: `Wanderly`

## 🎯 Future Roadmap

- [ ] **Cloud Sync** - Firebase integration for cross-device access
- [ ] **Maps Integration** - Google Maps for location visualization
- [ ] **Weather API** - Real-time weather forecasts for destinations
- [ ] **Booking Integration** - Direct flight and hotel booking
- [ ] **Multi-language** - Support for 10+ languages
- [ ] **Dark Mode** - Eye-friendly night theme
- [ ] **PDF Export** - Download itineraries as PDF documents
- [ ] **Social Sharing** - Share trips with friends
- [ ] **Collaborative Planning** - Multi-user trip planning
- [ ] **Offline Maps** - Download maps for offline use

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Ujjay

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 🙏 Acknowledgments

- **Groq** - For providing the blazing-fast LLM inference API
- **Google** - For Android SDK and Material Design components
- **OkHttp** - For reliable HTTP networking
- **Gson** - For seamless JSON parsing
- **Android Open Source Project** - For the foundation of this app

## 📬 Contact & Support

- **Developer:** Ujjay
- **GitHub:** [@ujjay2808](https://github.com/ujjay2808)
- **Project Link:** [Wanderly-Android-App](https://github.com/ujjay2808/Wanderly-Android-App)
- **Issues:** [Report a Bug](https://github.com/ujjay2808/Wanderly-Android-App/issues)

---

<div align="center">

**If you find this project helpful, please consider giving it a ⭐!**

Made by [Ujjay](https://github.com/ujjay2808)

</div>
