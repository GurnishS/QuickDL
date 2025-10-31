# Troubleshooting Guide for QuickDL Development

This guide covers common issues you might encounter when developing QuickDL in GitHub Codespaces or locally.

## Build Issues

### Issue: "Plugin [id: 'com.android.application'] was not found"

**Symptoms:**
```
FAILURE: Build failed with an exception.
Plugin [id: 'com.android.application', version: '8.9.0'] was not found
```

**Cause:** Network connectivity issues or missing Gradle dependencies.

**Solutions:**

1. **Check internet connectivity:**
   ```bash
   ping -c 2 google.com
   ```

2. **Clear Gradle cache and retry:**
   ```bash
   ./gradlew clean --refresh-dependencies
   rm -rf ~/.gradle/caches/
   ./gradlew assembleDebug
   ```

3. **Verify repositories in settings.gradle.kts:**
   Ensure these repositories are configured:
   - google()
   - mavenCentral()
   - gradlePluginPortal()

---

### Issue: "SDK location not found"

**Symptoms:**
```
SDK location not found. Define location with sdk.dir in the local.properties file
```

**Cause:** ANDROID_HOME environment variable not set or local.properties missing.

**Solutions:**

1. **Check environment variables:**
   ```bash
   echo $ANDROID_HOME
   echo $ANDROID_SDK_ROOT
   ```

2. **Create local.properties file:**
   ```bash
   echo "sdk.dir=$ANDROID_HOME" > local.properties
   ```

3. **Set environment variables (if missing):**
   ```bash
   export ANDROID_HOME=/usr/local/lib/android/sdk
   export ANDROID_SDK_ROOT=/usr/local/lib/android/sdk
   ```

---

### Issue: "Chaquopy: Python version not found" or "No Python installation found"

**Symptoms:**
```
Error: Chaquopy requires Python 3.8 or higher
```

**Cause:** Python 3.11+ not installed or not in PATH.

**Solutions:**

1. **Check Python version:**
   ```bash
   python3 --version
   ```

2. **Install Python 3.11+ (Ubuntu/Debian):**
   ```bash
   sudo apt update
   sudo apt install python3.11 python3-pip -y
   ```

3. **Create symbolic link if needed:**
   ```bash
   sudo ln -sf /usr/bin/python3.11 /usr/bin/python3
   ```

---

### Issue: "Failed to install yt-dlp" during build

**Symptoms:**
```
Error: Could not install package 'yt-dlp'
```

**Cause:** Network issues or pip not configured correctly.

**Solutions:**

1. **Ensure internet connectivity:**
   The first build requires internet to download yt-dlp.

2. **Check pip installation:**
   ```bash
   pip3 --version
   python3 -m pip --version
   ```

3. **Manually install yt-dlp to verify:**
   ```bash
   pip3 install yt-dlp
   ```

4. **Clean and rebuild:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

---

## Environment Issues

### Issue: "Permission denied" when running gradlew

**Symptoms:**
```bash
bash: ./gradlew: Permission denied
```

**Cause:** gradlew file doesn't have execute permissions.

**Solution:**
```bash
chmod +x gradlew
./gradlew --version
```

---

### Issue: Android Platform or Build Tools not found

**Symptoms:**
```
Failed to find Platform SDK with path: platforms;android-35
Failed to find Build Tools revision 35.0.0
```

**Cause:** Required Android SDK components not installed.

**Solutions:**

1. **List installed components:**
   ```bash
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --list_installed
   ```

2. **Install missing components:**
   ```bash
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platforms;android-35"
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "build-tools;35.0.0"
   ```

3. **Accept licenses:**
   ```bash
   yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
   ```

---

### Issue: NDK not found (required for Chaquopy)

**Symptoms:**
```
Error: NDK is not configured
Chaquopy requires NDK to be installed
```

**Cause:** Android NDK not installed.

**Solutions:**

1. **Install NDK:**
   ```bash
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "ndk;26.3.11579264"
   ```

2. **Verify installation:**
   ```bash
   ls $ANDROID_HOME/ndk/
   ```

---

## Codespace-Specific Issues

### Issue: Codespace runs out of memory during build

**Symptoms:**
- Build process killed
- "Out of memory" errors
- Gradle daemon crashes

**Solutions:**

1. **Increase Gradle memory:**
   Add to `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
   ```

2. **Disable Gradle daemon:**
   ```bash
   ./gradlew --no-daemon assembleDebug
   ```

3. **Use a larger Codespace machine type:**
   - Go to Codespace settings
   - Change machine type to 4-core or 8-core

---

### Issue: Slow build times in Codespace

**Symptoms:**
- Build takes 10+ minutes
- Gradle sync is very slow

**Solutions:**

1. **Enable Gradle build cache:**
   Add to `gradle.properties`:
   ```properties
   org.gradle.caching=true
   ```

2. **Use Gradle daemon (default):**
   Remove `--no-daemon` flag from commands

3. **Check network speed:**
   Slow first builds are normal as dependencies download

---

### Issue: Changes not reflected after rebuild

**Symptoms:**
- Code changes don't appear in APK
- Old version keeps running

**Solutions:**

1. **Clean build:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

2. **Invalidate caches:**
   ```bash
   rm -rf app/build/
   rm -rf build/
   ./gradlew assembleDebug
   ```

---

## Testing Issues

### Issue: "No connected devices" when running tests

**Symptoms:**
```
Error: No connected devices found
```

**Cause:** Trying to run instrumented tests without an emulator or device.

**Solutions:**

1. **Run unit tests instead:**
   ```bash
   ./gradlew test
   ```

2. **Skip instrumented tests:**
   ```bash
   ./gradlew assembleDebug -x connectedAndroidTest
   ```

3. **Set up an emulator (if needed):**
   ```bash
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "system-images;android-35;google_apis;x86_64"
   $ANDROID_HOME/cmdline-tools/latest/bin/avdmanager create avd -n test -k "system-images;android-35;google_apis;x86_64"
   ```

---

## Dependency Issues

### Issue: "Could not resolve all dependencies"

**Symptoms:**
```
Could not resolve com.example:library:1.0.0
```

**Cause:** Network issues or missing repositories.

**Solutions:**

1. **Refresh dependencies:**
   ```bash
   ./gradlew clean --refresh-dependencies
   ```

2. **Check repository configuration:**
   Verify `settings.gradle.kts` has:
   ```kotlin
   repositories {
       google()
       mavenCentral()
   }
   ```

3. **Clear dependency cache:**
   ```bash
   rm -rf ~/.gradle/caches/
   ./gradlew assembleDebug
   ```

---

## General Debugging

### Check Environment

Always start by verifying your environment:

```bash
./verify_env.sh
```

This will check:
- Java version (must be 17+)
- JAVA_HOME variable
- Android SDK installation
- Platform SDK (35)
- Build Tools
- NDK
- Python version (must be 3.11+)
- Gradle wrapper

### Enable Verbose Logging

For more detailed error messages:

```bash
./gradlew assembleDebug --info
# or for even more detail
./gradlew assembleDebug --debug
```

### Check Logs

Build logs are in:
- `app/build/outputs/logs/`
- Gradle daemon logs: `~/.gradle/daemon/*/daemon-*.out.log`

### Clean Everything

When in doubt, clean everything:

```bash
./gradlew clean
rm -rf ~/.gradle/caches/
rm -rf app/build/
rm -rf build/
./gradlew assembleDebug
```

---

## Getting Help

If none of these solutions work:

1. **Check the documentation:**
   - [CODESPACE_SETUP.md](CODESPACE_SETUP.md) - Setup guide
   - [QUICKSTART.md](QUICKSTART.md) - Quick start
   - [README.md](README.md) - Project overview

2. **Search for the error:**
   - Google the exact error message
   - Check Stack Overflow
   - Search GitHub issues

3. **Ask for help:**
   - Open an issue on GitHub with:
     - Output from `./verify_env.sh`
     - Full error message
     - Steps to reproduce
     - Output from `./gradlew assembleDebug --stacktrace`

---

## Quick Reference

```bash
# Environment verification
./verify_env.sh

# Clean build
./gradlew clean
./gradlew assembleDebug

# Verbose build
./gradlew assembleDebug --info --stacktrace

# Refresh dependencies
./gradlew clean --refresh-dependencies

# Check Android SDK
echo $ANDROID_HOME
ls $ANDROID_HOME/platforms/
ls $ANDROID_HOME/build-tools/

# Check Java
java -version
echo $JAVA_HOME

# Check Python
python3 --version

# List Gradle tasks
./gradlew tasks
```

---

**Still having issues?** Open an issue on GitHub with detailed information!
