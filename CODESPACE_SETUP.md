# Setting Up QuickDL in GitHub Codespaces

This guide will help you set up the development environment for QuickDL, a Kotlin Android app, in GitHub Codespaces.

## Automatic Setup (Recommended)

The repository includes a `.devcontainer` configuration that automatically sets up the development environment when you open it in GitHub Codespaces.

### Steps:

1. **Open in Codespaces**:
   - Go to the GitHub repository
   - Click the green "Code" button
   - Select the "Codespaces" tab
   - Click "Create codespace on main" (or your preferred branch)

2. **Wait for Setup**:
   - The devcontainer will automatically install:
     - Java 17 (JDK)
     - Gradle build tool
     - Android SDK (Platform 35)
     - Build Tools (35.0.0)
     - NDK (26.3.11579264)
     - Python 3.11 (required for Chaquopy/yt-dlp)

3. **Verify Installation**:
   ```bash
   java -version          # Should show Java 17
   ./gradlew --version    # Should show Gradle 8.11.1
   echo $ANDROID_HOME     # Should show Android SDK path
   ```

## Manual Setup

If you're not using the devcontainer or need to set up manually:

### Prerequisites

- Java 17 or higher
- Android SDK with:
  - Platform SDK 35 (compileSdk)
  - Build Tools 35.0.0 or higher
  - NDK (for Chaquopy support)
- Python 3.11+ (for Chaquopy)
- Gradle (or use the wrapper)

### Installation Steps

#### 1. Install Java 17

```bash
# For Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk -y
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### 2. Install Android SDK

Download and install Android Command Line Tools:

```bash
# Create SDK directory
mkdir -p ~/Android/Sdk
cd ~/Android/Sdk

# Download command line tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true

# Set environment variables
export ANDROID_HOME=~/Android/Sdk
export ANDROID_SDK_ROOT=~/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
```

#### 3. Install Required Android Components

```bash
# Accept licenses
yes | sdkmanager --licenses

# Install required components
sdkmanager "platform-tools" \
           "platforms;android-35" \
           "build-tools;35.0.0" \
           "ndk;26.3.11579264" \
           "cmake;3.22.1"
```

#### 4. Install Python 3.11+

```bash
# For Ubuntu/Debian
sudo apt update
sudo apt install python3.11 python3-pip -y
```

#### 5. Make gradlew executable

```bash
cd /path/to/QuickDL
chmod +x gradlew
```

### Environment Variables

Add these to your `~/.bashrc` or `~/.zshrc`:

```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=~/Android/Sdk
export ANDROID_SDK_ROOT=~/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator
```

Then reload:
```bash
source ~/.bashrc
```

## Building the Project

Once the environment is set up:

### 1. Sync Dependencies

```bash
./gradlew clean
```

### 2. Build the Project

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### 3. Run Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator or device)
./gradlew connectedAndroidTest
```

## Common Issues and Solutions

### Issue 1: "Permission denied" when running gradlew

**Solution:**
```bash
chmod +x gradlew
```

### Issue 2: "ANDROID_HOME not set"

**Solution:**
```bash
export ANDROID_HOME=~/Android/Sdk
export ANDROID_SDK_ROOT=~/Android/Sdk
```

### Issue 3: "Failed to install Chaquopy plugin" or Python-related errors

**Solution:**
Ensure Python 3.11+ is installed:
```bash
python3 --version
# If not 3.11+, install it
sudo apt install python3.11 -y
```

### Issue 4: Build fails with "SDK location not found"

**Solution:**
Create a `local.properties` file in the project root:
```bash
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

### Issue 5: Network connectivity issues in Codespaces

If you experience network issues when downloading dependencies:
- The first build requires internet access to download Gradle dependencies
- Ensure your Codespace has proper network connectivity
- If behind a proxy, configure Gradle proxy settings in `gradle.properties`

## Working with Chaquopy

This project uses Chaquopy to run Python (yt-dlp) in Android. Key considerations:

1. **First Build**: The first build downloads `yt-dlp` and its dependencies. This requires internet access.
2. **Python Version**: Chaquopy requires Python 3.11+
3. **NDK**: Chaquopy requires NDK to be installed (specified in build.gradle.kts)

## VS Code Extensions (Optional but Recommended)

Install these extensions for better development experience:

- **Kotlin Language** (`mathiasfrohlich.Kotlin`)
- **Java Extension Pack** (`vscjava.vscode-java-pack`)
- **Gradle for Java** (`vscjava.vscode-gradle`)
- **Python** (`ms-python.python`)

These are automatically installed if using the devcontainer.

## Next Steps

After successful setup:

1. **Explore the codebase**: Start with `MainActivity.kt` and the UI components
2. **Run the app**: You'll need an Android emulator or physical device
3. **Make changes**: The hot reload should work with Jetpack Compose
4. **Read the main README**: For more information about the project features

## Setting Up an Android Emulator

To test the app, you can set up an Android Virtual Device (AVD):

```bash
# Create an AVD (if sdkmanager is configured)
sdkmanager "system-images;android-35;google_apis;x86_64"
avdmanager create avd -n test_device -k "system-images;android-35;google_apis;x86_64"

# Launch emulator
emulator -avd test_device
```

**Note**: Running an emulator in Codespaces may have performance limitations. Physical device testing via ADB over network is recommended.

## Support

If you encounter issues not covered here:
1. Check the [main README](README.md) for project-specific information
2. Review build.gradle.kts for dependency requirements
3. Open an issue on GitHub with details about the error

## Quick Reference

```bash
# Verify environment
java -version                    # Should be 17+
./gradlew --version              # Should be 8.11.1+
echo $ANDROID_HOME               # Should point to SDK
python3 --version                # Should be 3.11+

# Common commands
./gradlew clean                  # Clean build
./gradlew assembleDebug          # Build debug APK
./gradlew test                   # Run tests
./gradlew tasks                  # List all available tasks
```
