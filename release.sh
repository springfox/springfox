echo  "Using GITHUB_TOKEN=$GITHUB_TOKEN, "\
      "BINTRAY_USER_NAME=$BINTRAY_USER_NAME, "\
      "BINTRAY_PASSWORD=$BINTRAY_PASSWORD, "\
      "DRYRUN=$DRYRUN"
./gradlew release releaseDocs \
  -PgithubToken=$GITHUB_TOKEN \
  -PbintrayUsername=$BINTRAY_USER_NAME \
  -PbintrayPassword=$BINTRAY_PASSWORD \
  -PdryRun=$DRYRUN -i -x check
