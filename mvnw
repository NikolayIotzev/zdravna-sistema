#!/bin/sh
# Maven Wrapper script

set -e

# Find project base directory
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Download Maven if not present
if [ ! -f "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" ]; then
    echo "Downloading Maven wrapper..."

    if command -v curl > /dev/null 2>&1; then
        curl -sL -o "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
            "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
            "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    else
        echo "Error: curl or wget is required to download Maven wrapper"
        exit 1
    fi
fi

# Find Java
if [ -n "$JAVA_HOME" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="java"
fi

# Run Maven wrapper
exec "$JAVA_CMD" \
    -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
    -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"
