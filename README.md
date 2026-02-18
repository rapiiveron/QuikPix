# QuikPix 📸✨

**QuikPix** is a modern, category-based Android gallery app that organizes your photos intelligently by folders. Built with **Jetpack Compose**, **Material Design 3**, and real-time **MediaStore** integration.

![QuikPix](https://img.shields.io/badge/QuikPix-v2.0-purple.svg)
![Android](https://img.shields.io/badge/Android-8.0%2B-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-orange.svg)
![Compose](https://img.shields.io/badge/Compose-2024.02.00-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## ✨ Features

### 🏠 Smart Category Organization
- **Automatic Folder Detection** - Scans device storage and groups photos by folders
- **Category Cards** - Beautiful cards showing folder name, thumbnail collage, and item count
- **Intelligent Sorting** - Sort by Recent, Name (A-Z), Item Count, or Pinned status
- **Real-time Updates** - Automatically detects new photos and folders
- **Crash Prevention** - Limits to 20 images per category to prevent memory issues

### 📁 Category Examples
- **Camera** (`/DCIM/Camera`) - Your camera photos
- **Screenshots** (`/Pictures/Screenshots`) - All screenshots
- **WhatsApp** - WhatsApp media organized automatically (limited to 20 images to prevent crashes)
- **Downloads** - Downloaded images
- **Instagram** - Instagram saved photos
- **Custom Folders** - Any folder you create

### 🎨 Modern UI & UX
- **Jetpack Compose** - Smooth, responsive UI with 60fps animations
- **Material Design 3** - Purple-blue gradient theme matching app icon
- **Adaptive Layout** - 2-column grid on phones, 3+ columns on tablets
- **Smooth Transitions** - Crossfade animations between screens
- **Gesture Support** - Swipe, pinch-to-zoom, double-tap zoom

### 🔍 Advanced Media Management
- **MediaStore Integration** - Direct access to device photos (no copying)
- **Thumbnail Collage** - Shows thumbnail from each folder
- **Folder Statistics** - Item count and last modified date
- **Permission Handling** - Android version-specific permissions (8.0 to 15+) with settings dialog
- **Performance Optimized** - Loads up to 50 categories with lazy loading
- **Crash Protection** - Limits to 20 images per category

### 📱 Permissions
- **Android 8.0-10**: READ_EXTERNAL_STORAGE
- **Android 11-13**: READ_MEDIA_IMAGES, READ_MEDIA_VIDEO
- **Android 14+**: READ_MEDIA_VISUAL_USER_SELECTED

### 🏗️ Architecture & Tech Stack
- **MVVM with Clean Architecture** - Separation of concerns
- **Kotlin Coroutines & Flow** - Asynchronous operations with reactive streams
- **MediaStore API** - Direct access to device media database
- **Coil** - Efficient image loading with caching
- **Repository Pattern** - Centralized data access
- **State Hoisting** - Predictable UI state management
- **Dependency Injection** - Manual DI (simplified for performance)

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
QuikPix/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/quikpix/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   └── Category.kt          # Category data class (Parcelable)
│   │   │   │   │   └── repository/
│   │   │   │   │       └── CategoryRepository.kt # MediaStore queries for categories
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── SimpleCategoriesActivity.kt  # Main activity with category grid
│   │   │   │   │   ├── Phase3ReadyActivity.kt       # Legacy gallery with viewer
│   │   │   │   │   └── screens/
│   │   │   │   │       ├── CategoriesScreen.kt      # 2-column category grid
│   │   │   │   │       └── CategoryDetailScreen.kt  # Images within a category
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── CategoriesViewModel.kt       # Manages category list
│   │   │   │   │   └── CategoryDetailViewModel.kt   # Manages images in category
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt                 # Color palette
│   │   │   │           ├── Theme.kt                 # Material Design 3 theme
│   │   │   │           └── Type.kt                  # Typography
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml   # Purple-blue gradient
│   │   │   │   │   └── ic_launcher_foreground.xml   # Crystalline aperture icon
│   │   │   │   ├── mipmap-*/                        # Adaptive icons (5 densities)
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml                   # Color resources
│   │   │   │   │   ├── strings.xml                  # String resources
│   │   │   │   │   └── themes.xml                   # App theme
│   │   │   │   └── xml/
│   │   │   │       └── file_paths.xml               # File provider paths
│   │   │   └── AndroidManifest.xml                  # App manifest
│   │   └── test/
│   ├── build.gradle.kts                             # App build configuration
│   └── proguard-rules.pro                           # ProGuard rules
├── gradle.properties                                # Gradle properties
├── build.gradle.kts                                 # Project build configuration
├── settings.gradle.kts                              # Settings configuration
└── README.md                                        # This file
```

## 🎨 Design & Branding

### App Icon
QuikPix features a modern, crystalline aperture icon:
- **Gradient Background**: Purple (#8A2BE2) → Slate Blue (#6A5ACD) → Deep Sky Blue (#00BFFF)
- **Foreground Elements**: 
  - White photo frame with dark blue center
  - Hexagonal camera aperture (white)
  - Sun/circle element (top-right)
  - Crystalline facets (transparent triangles)
- **Adaptive Icon**: Supports round, square, and squircle shapes

### Color Palette
- **Primary Gradient**: Purple (#8A2BE2) → Blue (#00BFFF)
- **Surface Colors**: Material Design 3 dynamic colors
- **Text Colors**: High contrast for readability
- **Accent Colors**: Purple-blue spectrum throughout UI

### Typography
- **Headlines**: Roboto Bold
- **Body Text**: Roboto Regular  
- **Captions**: Roboto Medium
- **Material Design 3** typography scale

## 🔐 Privacy & Permissions

### Data Privacy
- **No Cloud Sync**: All data stays on your device
- **No Internet Access**: Works completely offline
- **No Data Collection**: No analytics, tracking, or telemetry
- **Local Processing**: All image processing happens on-device

### Permissions
QuikPix requests minimal permissions based on Android version:
- **Android 8.0-10**: `READ_EXTERNAL_STORAGE`
- **Android 11-13**: `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`
- **Android 14+**: `READ_MEDIA_VISUAL_USER_SELECTED`

### MediaStore Integration
- **Direct Access**: Reads from Android's MediaStore database
- **No File Copying**: Doesn't duplicate or modify your photos
- **Read-Only**: Cannot delete or edit your media files
- **Folder-Based**: Organizes by existing folder structure

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📧 Contact & Support

- **Developer**: Raffie (@forevxr)
- **Platform**: Android 8.0+ (API 26+)
- **Repository**: Private (contact for access)
- **Telegram**: @forevxr for feedback and suggestions

## 🙏 Acknowledgments & Technologies

### Core Technologies
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern declarative UI toolkit
- **[Material Design 3](https://m3.material.io/)** - Google's design system
- **[Coil](https://coil-kt.com/)** - Kotlin image loading library
- **[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming
- **[Android MediaStore](https://developer.android.com/training/data-storage/shared/media)** - Media database access

### Design Inspiration
- **Microsoft Photos** - Folder-based organization
- **Google Photos** - Clean, intuitive interface  
- **iOS Photos App** - Smooth animations and gestures
- **Material Design 3** - Dynamic color theming

### Development Tools
- **Android Studio** - Official IDE for Android development
- **Git** - Version control system
- **Gradle** - Build automation tool
- **ADB** - Android Debug Bridge for testing

## 📝 Changelog

### Version 2.0 - QuikPix (Current)
**Category-Based Gallery with Modern UI**

#### ✅ Implemented
- **Category System**: Automatic folder detection and organization
- **MediaStore Integration**: Real-time photo scanning by folders
- **Modern Icon**: Crystalline aperture design with gradient background
- **Material Design 3**: Purple-blue theme with smooth animations
- **Permission Handling**: Android 8.0-15+ compatibility with settings dialog
- **Responsive UI**: 2-column grid with adaptive layout
- **Performance**: Lazy loading, Coil image caching, 20-image limit per category
- **WhatsApp Support**: Works without crashing (limited to 20 images)

#### 🚧 In Development
- **Category Detail Screen**: View all images in a folder (images limited to 20 per category)
- **Sorting Options**: Recent, Name, Count, Pinned
- **Search Functionality**: Find folders by name
- **Pull-to-Refresh**: Manual category refresh
- **Empty States**: Better UX for no photos scenarios
- **Fullscreen Image Viewer**: Basic implementation in progress

#### 📋 Planned Features
- **Video Support**: Play videos within categories
- **Favorite Folders**: Pin important categories
- **Batch Operations**: Select multiple images
- **Sharing**: Share images from within app
- **Dark Mode**: Automatic theme switching
- **Backup/Restore**: Export category organization

---

## 🚀 Quick Start

### For Users
1. Download the APK from releases
2. Install on Android 8.0+ device
3. Grant storage permissions when prompted
4. Browse your photos organized by folders!

### For Developers
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Build and run on device/emulator
5. Contribute improvements!

---

**QuikPix** - Your photos, organized by folders. Quickly. ✨

*Built with ❤️ for Android users who want smart photo organization without the cloud.*
