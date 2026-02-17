# FastGallery Pro 🖼️

A modern, fast, and beautiful Android gallery application built with **Jetpack Compose**, **MVVM architecture**, and **Hilt** for dependency injection.

![FastGallery Pro](https://img.shields.io/badge/FastGallery%20Pro-v1.0-blue.svg)
![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-orange.svg)
![Compose](https://img.shields.io/badge/Compose-2024.02.00-purple.svg)

## ✨ Features

### 🎨 Modern UI
- **Jetpack Compose** for beautiful, responsive UI
- **Material Design 3** with custom purple theme
- Smooth animations and transitions
- Dark/Light theme support (planned)

### 🗂️ Media Management
- **Photo Gallery** - Browse all images in your device
- **Video Player** - Watch videos with ExoPlayer (planned)
- **Albums** - Organize media by albums (planned)
- **Hidden Items** - Privacy mode for sensitive photos (planned)

### 📱 Permissions
- **Android 8.0-10**: READ_EXTERNAL_STORAGE
- **Android 11-13**: READ_MEDIA_IMAGES, READ_MEDIA_VIDEO
- **Android 14+**: READ_MEDIA_VISUAL_USER_SELECTED

### 🏗️ Architecture
- **MVVM** - Clean separation of concerns
- **Hilt** - Dependency injection
- **Room** - Local database for hidden items
- **Coil** - Image loading
- **Coroutines** - Asynchronous operations

## 📋 Requirements

### Development
- **Android Studio**: Hedgehog | 2023.1.1 or higher
- **JDK**: 17 or higher
- **Android SDK**: API 26 (Android 8.0) - 35 (Android 15)
- **Gradle**: 8.9
- **Kotlin**: 1.9.23

### Runtime
- **Android**: 8.0 (API 26) or higher
- **Storage**: Permission required for media access

## 🚀 Installation

### From APK
1. Download the latest `app-debug.apk` from the [Releases](https://github.com/yourusername/FastGalleryPro/releases) page
2. Enable **Unknown Sources** on your device
3. Install the APK
4. Open the app and grant storage permissions

### From Android Studio
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/FastGalleryPro.git
   cd FastGalleryPro
   ```

2. Open in Android Studio:
   - Open **File** → **Open**
   - Select the `FastGalleryPro` folder
   - Click **OK**

3. Sync Gradle:
   - Click **"Sync Now"** when prompted

4. Build the app:
   - Go to **Build** → **Make Project**
   - Wait for **BUILD SUCCESSFUL**

5. Install and run:
   - Click the green **Play** button (▶️)
   - Select your device/emulator
   - The app will install and launch

## 📦 Building

### Build Debug APK
```bash
gradlew.bat clean assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK
```bash
gradlew.bat clean assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Install on Device
```bash
gradlew.bat installDebug
```

## 🎯 Project Structure

```
FastGalleryPro/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fastgallery/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── database/
│   │   │   │   │   │   │   ├── GalleryDatabase.kt
│   │   │   │   │   │   │   └── HiddenMediaDao.kt
│   │   │   │   │   │   └── prefs/
│   │   │   │   │   │       └── UserPreferences.kt
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── MediaStoreItem.kt
│   │   │   │   │   ├── picker/
│   │   │   │   │   │   └── PhotoPickerManager.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── MediaRepository.kt
│   │   │   │   │       ├── MediaRepositoryImpl.kt
│   │   │   │   │       └── MediaPagingSource.kt
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt
│   │   │   │   └── presentation/
│   │   │   │       ├── MainActivity.kt
│   │   │   │       ├── screens/
│   │   │   │       │   ├── GalleryScreen.kt
│   │   │   │       │   ├── ViewerScreen.kt
│   │   │   │       │   └── UnifiedGalleryScreen.kt
│   │   │   │       ├── viewmodel/
│   │   │   │       │   ├── GalleryViewModel.kt
│   │   │   │       │   └── PickerViewModel.kt
│   │   │   │       └── components/
│   │   │   │           ├── AlbumCard.kt
│   │   │   │           ├── MediaGrid.kt
│   │   │   │           └── PickerToolbar.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── mipmap-*/
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       └── file_paths.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle.properties
├── build.gradle
├── settings.gradle.kts
└── README.md
```

## 🎨 Theme

FastGallery Pro uses a custom purple theme:
- **Primary Color**: #6650a4 (Purple)
- **Secondary Color**: #3700b3 (Deep Purple)
- **Surface Color**: #1E1E2E (Dark Blue-Grey)
- **Background Color**: #FFFFFF (White)

## 🔐 Privacy

- **Hidden Items**: Mark photos as hidden for privacy
- **No Cloud Sync**: All data stays on your device
- **Minimal Permissions**: Only requests necessary permissions
- **Open Source**: Full transparency of code

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📧 Contact

- **Email**: your.email@example.com
- **GitHub**: https://github.com/yourusername/FastGalleryPro
- **Telegram**: @yourusername

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Hilt](https://dagger.dev/hilt/) - Dependency injection for Android
- [Room](https://developer.android.com/training/data-storage/room) - SQLite abstraction
- [Coil](https://coil-kt.com/) - Image loading for Compose
- [Material Design 3](https://m3.material.io/) - Google's design system

## 📝 Changelog

### Version 1.0 (Coming Soon)
- ✅ Basic UI with Jetpack Compose
- ✅ Material Design 3 theme
- ✅ Permission handling for different Android versions
- ✅ Minimal "Hello World" screen
- 🚧 Gallery browsing (planned)
- 🚧 Photo viewer (planned)
- 🚧 Video player (planned)
- 🚧 Album management (planned)
- 🚧 Hidden items feature (planned)

---

Made with ❤️ by [Your Name]

**FastGallery Pro** - Your media, organized beautifully.
