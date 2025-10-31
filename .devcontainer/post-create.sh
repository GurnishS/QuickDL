#!/bin/bash

# Post-create script for QuickDL Codespace
# This script runs after the devcontainer is created

echo ""
echo "=========================================="
echo "   Welcome to QuickDL Development!"
echo "=========================================="
echo ""
echo "Setting up your environment..."
echo ""

# Make gradlew executable
if [ -f "gradlew" ]; then
    chmod +x gradlew
    echo "✓ Made gradlew executable"
fi

# Make verify script executable
if [ -f "verify_env.sh" ]; then
    chmod +x verify_env.sh
    echo "✓ Made verify_env.sh executable"
fi

echo ""
echo "=========================================="
echo "   Environment Setup Complete!"
echo "=========================================="
echo ""
echo "Quick start commands:"
echo ""
echo "  ./verify_env.sh           - Verify your environment"
echo "  ./gradlew tasks           - List all available Gradle tasks"
echo "  ./gradlew assembleDebug   - Build the debug APK"
echo ""
echo "Documentation:"
echo "  - QUICKSTART.md           - 2-minute quick start guide"
echo "  - CODESPACE_SETUP.md      - Detailed setup instructions"
echo "  - README.md               - Project overview"
echo ""
echo "Ready to code! 🚀"
echo "=========================================="
echo ""
