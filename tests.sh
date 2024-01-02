set -e
./gradlew publishToMavenLocal
./gradlew test
./gradlew -p "app" testDebugUnitTest