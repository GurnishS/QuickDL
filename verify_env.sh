#!/bin/bash

# QuickDL Environment Verification Script
# This script checks if your development environment is properly configured

echo "======================================"
echo "QuickDL Environment Verification"
echo "======================================"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track if all checks pass
ALL_CHECKS_PASSED=true

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $2"
    else
        echo -e "${RED}✗${NC} $2"
        ALL_CHECKS_PASSED=false
    fi
}

# Check Java
echo "Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION_STRING=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    # Handle both "17.0.16" and "1.8.0" version formats
    JAVA_MAJOR=$(echo $JAVA_VERSION_STRING | cut -d'.' -f1)
    if [ "$JAVA_MAJOR" = "1" ]; then
        # For older Java versions like 1.8.0, the major version is the second number
        JAVA_MAJOR=$(echo $JAVA_VERSION_STRING | cut -d'.' -f2)
    fi
    if [ "$JAVA_MAJOR" -ge 17 ] 2>/dev/null; then
        print_status 0 "Java 17+ is installed (found version: $JAVA_VERSION_STRING)"
    else
        print_status 1 "Java version is too old (need 17+, found: $JAVA_VERSION_STRING)"
    fi
else
    print_status 1 "Java is not installed"
fi

# Check JAVA_HOME
echo ""
echo "Checking JAVA_HOME..."
if [ -n "$JAVA_HOME" ]; then
    print_status 0 "JAVA_HOME is set: $JAVA_HOME"
else
    print_status 1 "JAVA_HOME is not set"
fi

# Check Android SDK
echo ""
echo "Checking Android SDK..."
if [ -n "$ANDROID_HOME" ]; then
    print_status 0 "ANDROID_HOME is set: $ANDROID_HOME"
    
    # Check if SDK directory exists
    if [ -d "$ANDROID_HOME" ]; then
        print_status 0 "Android SDK directory exists"
        
        # Check for platform-tools
        if [ -d "$ANDROID_HOME/platform-tools" ]; then
            print_status 0 "Platform tools are installed"
        else
            print_status 1 "Platform tools are missing"
        fi
        
        # Check for required platform (API 35)
        if [ -d "$ANDROID_HOME/platforms/android-35" ]; then
            print_status 0 "Android Platform 35 is installed"
        else
            print_status 1 "Android Platform 35 is missing (required by project)"
        fi
        
        # Check for build tools
        if [ -d "$ANDROID_HOME/build-tools" ] && [ "$(ls -A $ANDROID_HOME/build-tools 2>/dev/null)" ]; then
            LATEST_BUILD_TOOLS=$(ls $ANDROID_HOME/build-tools | sort -V | tail -n 1)
            print_status 0 "Build tools are installed (latest: $LATEST_BUILD_TOOLS)"
        else
            print_status 1 "Build tools are missing"
        fi
        
        # Check for NDK (required by Chaquopy)
        if [ -d "$ANDROID_HOME/ndk" ] && [ "$(ls -A $ANDROID_HOME/ndk 2>/dev/null)" ]; then
            LATEST_NDK=$(ls $ANDROID_HOME/ndk | sort -V | tail -n 1)
            print_status 0 "NDK is installed (version: $LATEST_NDK)"
        else
            print_status 1 "NDK is missing (required for Chaquopy)"
        fi
    else
        print_status 1 "Android SDK directory does not exist"
    fi
else
    print_status 1 "ANDROID_HOME is not set"
fi

# Check for ANDROID_SDK_ROOT
echo ""
echo "Checking ANDROID_SDK_ROOT..."
if [ -n "$ANDROID_SDK_ROOT" ]; then
    print_status 0 "ANDROID_SDK_ROOT is set: $ANDROID_SDK_ROOT"
else
    print_status 1 "ANDROID_SDK_ROOT is not set (some tools may require it)"
fi

# Check Python
echo ""
echo "Checking Python..."
if command -v python3 &> /dev/null; then
    PYTHON_VERSION_STRING=$(python3 --version 2>&1 | awk '{print $2}')
    PYTHON_MAJOR=$(echo $PYTHON_VERSION_STRING | cut -d'.' -f1)
    PYTHON_MINOR=$(echo $PYTHON_VERSION_STRING | cut -d'.' -f2)
    
    # Check if major version is 3 and minor is >= 11, or major version is > 3
    if [ "$PYTHON_MAJOR" -gt 3 ] 2>/dev/null || ([ "$PYTHON_MAJOR" -eq 3 ] 2>/dev/null && [ "$PYTHON_MINOR" -ge 11 ] 2>/dev/null); then
        print_status 0 "Python 3.11+ is installed (found: $(python3 --version))"
    else
        print_status 1 "Python version is too old (need 3.11+, found: $(python3 --version))"
    fi
else
    print_status 1 "Python 3 is not installed (required for Chaquopy)"
fi

# Check Gradle wrapper
echo ""
echo "Checking Gradle..."
if [ -f "./gradlew" ]; then
    if [ -x "./gradlew" ]; then
        print_status 0 "Gradle wrapper exists and is executable"
        GRADLE_VERSION=$(./gradlew --version 2>/dev/null | grep "Gradle" | head -n 1)
        if [ $? -eq 0 ]; then
            print_status 0 "$GRADLE_VERSION"
        fi
    else
        print_status 1 "Gradle wrapper exists but is not executable (run: chmod +x gradlew)"
    fi
else
    print_status 1 "Gradle wrapper (gradlew) not found"
fi

# Check internet connectivity (optional)
echo ""
echo "Checking internet connectivity..."
if ping -c 1 google.com &> /dev/null; then
    print_status 0 "Internet connection is available"
elif ping -c 1 dl.google.com &> /dev/null; then
    print_status 0 "Internet connection is available"
else
    echo -e "${YELLOW}!${NC} Internet connection may be limited (first build requires internet)"
fi

# Summary
echo ""
echo "======================================"
if [ "$ALL_CHECKS_PASSED" = true ]; then
    echo -e "${GREEN}All checks passed!${NC} ✓"
    echo ""
    echo "You can now build the project:"
    echo "  ./gradlew assembleDebug"
else
    echo -e "${RED}Some checks failed!${NC} ✗"
    echo ""
    echo "Please fix the issues above before building."
    echo "See CODESPACE_SETUP.md for setup instructions."
fi
echo "======================================"
