# 📝 Emotional Diary

<p align="center">
  <img src="icon.png" width="600">
</p>

### A Secure Personal Diary Mobile Application

Emotional Diary is an Android-based personal diary application that allows users to record their daily experiences, manage diary entries, and understand their emotions through AI-powered emotion detection. The application combines diary management, Firebase cloud services, and generative AI to provide a convenient and personalized journaling experience.

## Features

- **User Registration & Login** — Create an account and sign in securely.
- **Email Verification** — Verify the registered email address.
- **Forgot Password** — Reset the account password through email.
- **Create Diary Entries** — Write and save personal diary entries.
- **View Diary Entries** — Access previously saved diaries.
- **Edit Diary Entries** — Update existing diary content.
- **Delete Diary Entries** — Remove unwanted diary entries.
- **Diary Search** — Find diary entries easily.
- **AI-Based Emotion Detection** — Automatically analyze diary content and identify the dominant emotion.
- **Six Emotion Categories** — Happy, Excited, Calm, Neutral, Sad, and Angry.
- **Emotion Summary** — View the distribution of detected emotions.
- **Emotion Visualization** — Display emotion statistics using a pie chart.
- **Profile Management** — View and update profile information.
- **Change Password** — Change the account password.
- **Theme Customization** — Choose Light, Dark, or System theme.
- **Notification Settings** — Manage notification preferences.
- **Logout** — Sign out of the application.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application logic |
| XML | User interface design |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Firebase Cloud Firestore | Cloud database |
| Firebase Storage | Image and file storage |
| Firebase AI Logic | Integration with generative AI |
| Gemini 3.6 Flash | AI-based emotion detection |
| Material Design | User interface and design components |

## AI-Based Emotion Detection

Emotional Diary uses Firebase AI Logic to interact with the pre-trained Gemini 3.6 Flash model.

When a user creates a diary entry, the application sends the diary content and relevant previous diary context to the AI model through a structured prompt. The model analyzes the overall meaning, emotional tone, events, and feelings to identify one dominant emotion.

The application supports six predefined emotions:

- 😊 Happy
- 😍 Excited
- 😌 Calm
- 😐 Neutral
- 😢 Sad
- 😡 Angry

The detected emotion is associated with the diary entry and can contribute to the emotion summary displayed in the application.

**Note:** The project uses a pre-trained generative AI model. A custom machine learning model was not trained for this application.

## Firebase Integration

The application uses Firebase services for its cloud-based functionality:

- **Firebase Authentication:** Handles user authentication and password-related operations.
- **Cloud Firestore:** Stores application data, including diary-related information.
- **Firebase Storage:** Supports image and file storage.
- **Firebase AI Logic:** Connects the Android application to the Gemini generative AI model.

## Application Screenshots

Add your application screenshots in this section.

Suggested screenshots:
- Login Screen
- Registration Screen
- Home Screen
- Add Diary Screen
- Diary List Screen
- Diary Details Screen
- Emotion Summary
- Profile Screen
- Settings Screen

Example Markdown syntax:

```markdown
![Home Screen](screenshots/home.png)
```

Create a `screenshots` folder in the repository and place your screenshot images inside it.

## Requirements

- Android Studio
- Java development support
- Android SDK
- A Firebase project
- Firebase configuration file (`google-services.json`)
- Internet connection for Firebase cloud services and AI emotion detection

## Installation and Setup

1. Clone this repository:

   ```bash
   git clone https://github.com/ashfaqlabib/EmotionalDiary.git
   ```

2. Open Android Studio.

3. Select **Open** and choose the cloned `EmotionalDiary` project folder.

4. Create or select a Firebase project in the Firebase Console.

5. Register the Android application in Firebase using the project's application ID.

6. Download `google-services.json` and place it in the app module directory:

   ```text
   EmotionalDiary/
   └── app/
       └── google-services.json
   ```

7. Enable Firebase Authentication and configure the required sign-in method.

8. Set up Cloud Firestore and Firebase Storage according to the application configuration.

9. Enable and configure Firebase AI Logic with the Gemini Developer API backend for AI emotion detection.

10. Sync the Gradle files and run the application on an Android device or emulator.

**Important:** Firebase configuration, required APIs, security rules, and AI access must be configured correctly before all features will work.

## Security and Privacy

- Firebase Authentication manages account authentication.
- Password management is handled through Firebase Authentication rather than storing plain-text passwords in the application's diary database.
- Firestore and Storage security rules should restrict access to authorized users' data.
- Do not upload private credentials, API keys, or personal user data to the public repository.

## Project Objectives

- Develop a user-friendly Android diary application.
- Provide secure user authentication.
- Enable efficient diary entry management.
- Integrate AI-based automatic emotion detection.
- Visualize emotional patterns through summary statistics.
- Use Firebase services for cloud-based data management.

## Future Improvements

Potential future improvements include:

- More advanced emotion analytics.
- Improved personalization of emotion analysis.
- Additional diary organization and filtering options.
- Enhanced privacy controls and data export features.

## Development Team

**Project Name:** Emotional Diary  
**Project Type:** Android Mobile Application  
**Development Language:** Java  
**Database:** Firebase Cloud Firestore  
**AI Model:** Gemini 3.6 Flash

## License

This project was developed for academic purposes. Add an appropriate open-source license if you intend to distribute or reuse the source code publicly.

---

**Emotional Diary — Record your moments. Understand your emotions.**
