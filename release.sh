GITHUB_TOKEN=$GITHUB_TOKEN ./gradlew release publishDocs -PbintrayUsername=$BINTRAY_USER_NAME -PbintrayPassword=$BINTRAY_PASSWORD -i -x check
