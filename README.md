
# 🃏 Planning Poker - Native Android App

**Planning Poker** is a native Android app developed in Kotlin, designed to facilitate agile Planning Poker sessions for effort estimation in a fun and interactive way.

## Tech Stack and Versions

| Component | Version |
|:----------|:--------|
| Kotlin | 1.9+ |
| Android SDK | 34 / 35 |
| Gradle | 8.11.1 |
| Java | 17 |
| Compose | Using Compose BOM |
| Hilt | Enabled |
| Firebase Firestore | Integrated |
| DataStore | Used for local persistence |
| Testing Libraries | JUnit, MockK, Turbine, Espresso |

## 🛠️ Installation

Follow these steps to set up the project locally:

1. **Install Android Studio:**  
   [Download Android Studio](https://developer.android.com/studio)

2. **Set up Command-line Tools:**  
   Verify and configure your Command-line Tools as per [official instructions](https://developer.android.com/tools#environment-variables).

3. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/poker-planning.git
   cd poker-planning
   ```

4. **Open the project:**
   - Open Android Studio.
   - Select **Open an existing project** and navigate to the cloned folder.

5. **Configure Firebase (Optional if using backend services):**
   - Create a Firebase project.
   - Add an Android app.
   - Download `google-services.json`.
   - Place the file under `app/src/main/`.

6. **Build and Run:**
   - Select an emulator or connect a physical device.
   - Click on **Run** (`Shift + F10`) or run:
     ```bash
     ./gradlew assembleDebug
     ```

### Requirements

- Android Studio Giraffe or later
- Android SDK 34+
- Kotlin 1.9+
- Java 17
- Gradle 8.0+

## ⚙️ Configuration

Ensure you have:

- A valid `local.properties` file with your Android SDK path:
  ```
  sdk.dir=/path/to/your/Android/sdk
  ```
- Proper Firebase configurations if backend services are needed.
- Internet permissions if using remote services:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```

## 🤝 How to Contribute

Contributions are welcome! 🚀

1. **Fork** the repository.
2. Create a **feature branch** (`git checkout -b feature/my-new-feature`).
3. **Commit** your changes (`git commit -m 'feat: Add new feature'`).
4. **Push** to your branch (`git push origin feature/my-new-feature`).
5. Open a **Pull Request**.

### Contribution Guidelines

- Follow the existing coding style.
- Write clear and descriptive PR titles and commit messages.
- Test your changes thoroughly before submitting.

## 🧹 Troubleshooting

| Issue | Solution |
|:------|:---------|
| Firebase services not found | Ensure `google-services.json` is in `app/src/main/` |
| Build fails due to missing SDK or wrong Java version | Confirm you have Java 17 and the Android SDK properly set |
| Emulator/device not recognized | Verify that the device is connected or the emulator is running |
| Unexpected crashes or Gradle sync issues | Run `Build > Clean Project` and `Build > Rebuild Project` |

### Useful commands:

```bash
./gradlew clean
./gradlew build
```

Or via Android Studio:
- **Build > Clean Project**
- **Build > Rebuild Project**
