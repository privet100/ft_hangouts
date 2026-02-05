#!/bin/bash

# Setup script for building ft_hangouts on 42 school computers
# Run this script on school Mac to install Android SDK in goinfre

set -e

echo "=== ft_hangouts Android SDK Setup for 42 ==="

# Check if goinfre exists
if [ ! -d ~/goinfre ]; then
    echo "Error: ~/goinfre not found. Are you on a 42 school computer?"
    exit 1
fi

# Set environment variables
export ANDROID_HOME=~/goinfre/android-sdk
export JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null || echo "")

# Check Java
if [ -z "$JAVA_HOME" ]; then
    echo "Warning: Java not found. Trying to use system Java..."
fi

echo "ANDROID_HOME=$ANDROID_HOME"

# Create directories
mkdir -p $ANDROID_HOME
cd ~/goinfre

# Download Android command line tools (Mac version)
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-mac-11076708_latest.zip"
CMDLINE_TOOLS_ZIP="commandlinetools-mac.zip"

if [ ! -f "$CMDLINE_TOOLS_ZIP" ]; then
    echo "Downloading Android command line tools..."
    curl -L -o $CMDLINE_TOOLS_ZIP $CMDLINE_TOOLS_URL
fi

# Extract and setup directory structure
echo "Extracting..."
unzip -q -o $CMDLINE_TOOLS_ZIP -d $ANDROID_HOME

# Fix directory structure for sdkmanager
mkdir -p $ANDROID_HOME/cmdline-tools/latest
if [ -d "$ANDROID_HOME/cmdline-tools/bin" ]; then
    mv $ANDROID_HOME/cmdline-tools/* $ANDROID_HOME/cmdline-tools/latest/ 2>/dev/null || true
fi

# Update PATH
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses
echo "Accepting licenses..."
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses > /dev/null 2>&1 || true

# Install required SDK components
echo "Installing SDK components (this may take a while)..."
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

echo ""
echo "=== Setup complete! ==="
echo ""
echo "Add these lines to your ~/.zshrc or run before building:"
echo ""
echo "export ANDROID_HOME=~/goinfre/android-sdk"
echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools"
echo ""
echo "Then build with:"
echo "  cd /path/to/ft_hangouts"
echo "  ./gradlew assembleDebug"
echo ""
echo "APK will be at: app/build/outputs/apk/debug/app-debug.apk"
