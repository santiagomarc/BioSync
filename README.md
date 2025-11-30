# BioSync 🌿

A modern health and wellness tracking app for Android, built with Kotlin and Firebase.

## 📱 About

BioSync is a personal health dashboard that helps users track their daily wellness metrics including water intake, BMI, and calorie recommendations. Designed with a minimalist dark theme and clean UI, it provides an intuitive experience for health-conscious users.

**CS312 Final Project** | Aligned with **UN SDG 3: Good Health & Well-being**

## ✨ Features

- **User Authentication** - Secure sign-up and login with Firebase Auth
- **Health Profile** - Store personal metrics (age, weight, height, activity level)
- **BMI Calculator** - Automatic BMI calculation with category display
- **Water Tracking** - Daily water intake tracking with progress visualization
- **Calorie Recommendations** - Personalized daily calorie goals based on BMR and activity level
- **Cloud Sync** - All data synced with Firebase Firestore

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Architecture**: Activity-based
- **Backend**: Firebase (Auth + Firestore)
- **UI**: Material Design with custom Lexend font family

## 📐 Health Calculations

| Metric | Formula |
|--------|---------|
| **BMI** | weight (kg) / height² (m) |
| **BMR** | Mifflin-St Jeor equation |
| **Water Goal** | weight (kg) × 35 ml |
| **Calorie Goal** | BMR × activity multiplier |

## 🎨 Design

- **Theme**: Minimalist dark (#0D0D0D background)
- **Typography**: Lexend font family (Regular, Medium, SemiBold, Bold)
- **Colors**: Gray palette with subtle accent colors
- **Style**: Rounded corners, soft shadows, clean hierarchy

## 📂 Project Structure

```
app/src/main/
├── java/com/example/biosync/
│   ├── WelcomeActivity.kt      # App launcher & auth check
│   ├── LoginActivity.kt        # User sign-in
│   ├── RegisterActivity.kt     # New user registration
│   ├── HomeActivity.kt         # Main dashboard
│   ├── models/
│   │   └── User.kt             # User data model
│   └── utils/
│       └── HealthCalculator.kt # Health calculation utilities
├── res/
│   ├── layout/                 # Activity layouts
│   ├── font/                   # Lexend font files
│   ├── drawable/               # Icons & backgrounds
│   └── values/                 # Colors, strings, themes
└── AndroidManifest.xml
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+
- Firebase project with Auth and Firestore enabled

### Setup

1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/BioSync.git
   ```

2. Open in Android Studio

3. Add your `google-services.json` to the `app/` directory

4. Sync Gradle and run on emulator or device

## 🔒 Firebase Configuration

This project requires Firebase. Set up:
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Enable Email/Password authentication
3. Create a Firestore database
4. Download `google-services.json` and place in `app/` directory

## 📄 License

This project is for educational purposes (CS312 Final Project).

---

*Built with ❤️ for better health*
