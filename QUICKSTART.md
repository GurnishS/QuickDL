# QuickDL - Quick Start Guide for Codespaces

Welcome to QuickDL! This guide will help you get started quickly with development in GitHub Codespaces.

## 🚀 Getting Started (2 minutes)

### Step 1: Open in Codespaces

1. Click the green **"Code"** button on the GitHub repository page
2. Select the **"Codespaces"** tab
3. Click **"Create codespace on main"**

Wait a few minutes for the environment to set up automatically.

### Step 2: Verify Your Environment

Once the codespace is ready, run:

```bash
./verify_env.sh
```

This will check that all required tools are installed:
- ✓ Java 17
- ✓ Android SDK Platform 35
- ✓ Build Tools
- ✓ NDK (for Chaquopy)
- ✓ Python 3.11+
- ✓ Gradle

### Step 3: Build the Project

```bash
# Clean and build
./gradlew clean assembleDebug
```

**Note:** The first build will take 5-10 minutes as it downloads dependencies and the yt-dlp package.

## 📋 Common Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Clean build
./gradlew clean

# List all available tasks
./gradlew tasks

# Verify environment setup
./verify_env.sh
```

## 🏗️ Project Structure

```
QuickDL/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/solvynix/quickdl/
│   │   │   │   ├── MainActivity.kt          # Main entry point
│   │   │   │   ├── ui/                      # UI components
│   │   │   │   │   ├── screens/            # Screen composables
│   │   │   │   │   ├── components/         # Reusable components
│   │   │   │   │   └── theme/              # Theme & styling
│   │   │   │   ├── services/               # Background services
│   │   │   │   └── models/                 # Data models
│   │   │   └── python/                     # Python (yt-dlp) code
│   │   ├── test/                           # Unit tests
│   │   └── androidTest/                    # Instrumented tests
│   └── build.gradle.kts                    # App-level build config
├── build.gradle.kts                        # Project-level build config
├── settings.gradle.kts                     # Project settings
├── .devcontainer/                          # Codespaces config
│   └── devcontainer.json
├── README.md                               # Project overview
├── CODESPACE_SETUP.md                      # Detailed setup guide
└── verify_env.sh                           # Environment check script
```

## 🔧 Key Technologies

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Python Integration:** Chaquopy (runs yt-dlp)
- **Video Player:** ExoPlayer
- **Database:** Room
- **Architecture:** MVVM

## 🎯 What You Can Do

1. **Download Videos:** From YouTube, Instagram, Facebook, and more
2. **Offline Operation:** yt-dlp runs completely offline via Chaquopy
3. **Parallel Downloads:** Multiple concurrent downloads
4. **Video Playback:** Built-in player with ExoPlayer
5. **Local Storage:** All metadata stored in Room database

## 🐛 Troubleshooting

### Build fails on first attempt

**Solution:** The first build downloads Chaquopy and yt-dlp. If it fails:
```bash
./gradlew clean
./gradlew assembleDebug --refresh-dependencies
```

### "Permission denied" error

**Solution:**
```bash
chmod +x gradlew
```

### Environment issues

**Solution:**
```bash
./verify_env.sh
```

Check the output for any missing components.

### Network issues

If you're having connectivity problems:
- Codespaces requires internet for the first build
- Once dependencies are cached, most operations work offline
- Chaquopy downloads yt-dlp during the first build

**For more detailed troubleshooting**, see **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)**

## 📚 Learn More

- **[Troubleshooting Guide](TROUBLESHOOTING.md)** - Common issues and solutions
- **[Full Setup Guide](CODESPACE_SETUP.md)** - Detailed setup instructions
- **[Main README](README.md)** - Project features and overview
- **[Chaquopy Docs](https://chaquo.com/chaquopy/)** - Python integration
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - UI framework
- **[yt-dlp](https://github.com/yt-dlp/yt-dlp)** - Video downloader

## 🎉 Next Steps

1. ✅ **Explore the code** - Start with `MainActivity.kt`
2. ✅ **Make changes** - Try modifying the UI in `ui/components/`
3. ✅ **Test locally** - Connect a device or use an emulator
4. ✅ **Read the docs** - Check out the technologies used
5. ✅ **Contribute** - Follow the project contribution guidelines

## 💡 Tips

- **Hot Reload:** Jetpack Compose supports live previews in Android Studio
- **Gradle Daemon:** Subsequent builds are much faster (30-60 seconds)
- **Cache:** Dependencies are cached in the volume mount
- **Testing:** Unit tests run quickly without an emulator

---

**Happy Coding! 🚀**

For questions or issues, check the [main README](README.md) or open an issue on GitHub.
