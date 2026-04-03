set -e
./gradlew publishToMavenLocal
./gradlew test
./gradlew -p "demo-app" testDebugUnitTest