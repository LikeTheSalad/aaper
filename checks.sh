set -e
./gradlew check
./gradlew -p "demo-app" testDebugUnitTest
