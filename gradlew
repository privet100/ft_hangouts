#!/bin/sh

#
# Gradle start up script for POSIX
#

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Download wrapper jar if missing
if [ ! -f "$GRADLE_WRAPPER_JAR" ]; then
    echo "Downloading Gradle Wrapper..."
    mkdir -p "$APP_HOME/gradle/wrapper"
    curl -L -o "$GRADLE_WRAPPER_JAR" \
        "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
fi

# Determine the Java command
if [ -n "$JAVA_HOME" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Check java exists
if ! command -v "$JAVACMD" >/dev/null 2>&1; then
    echo "ERROR: JAVA_HOME is not set and 'java' not found in PATH"
    exit 1
fi

# Set Android SDK if in goinfre
if [ -d "$HOME/goinfre/android-sdk" ]; then
    export ANDROID_HOME="$HOME/goinfre/android-sdk"
    export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"
fi

exec "$JAVACMD" \
    -Xmx64m -Xms64m \
    -Dorg.gradle.appname=gradlew \
    -classpath "$GRADLE_WRAPPER_JAR" \
    org.gradle.wrapper.GradleWrapperMain "$@"
