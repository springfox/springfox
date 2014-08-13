### Development Environment

- File >> open >> build.gradle
- Make sure to check the 'use the default gradle wrapper' option.
- First time build

```bash
./gradlew clean build

```

- Code quality (code coverage, checkstyle)

```bash
./gradlew check
```
- Reports: `swagger-springmvc/build/reports` and `/swagger-models/build/reports`
- Coverage limits: coverage.gradle `minCoverage = 80` 
- To get more output from ant gralde commands/tasks append a `-i` (info) or `-d` (debug)

### Releasing
- The following command will publish the artifacts to bintray/jcenter. Only 'release' versions are hosted on bintray. 
This command will fail if you try to publish a release version that has already been published.

```bash
./gradlew -Pusername=<bintrayusername> -Ppassword=<bintraytoken> publish
```

- When a non-snapshot version has been published, Log in to bintray (https://bintray
.com/swaggerspringmvc/swaggerspringmvc) and publish and tweet the new version for swagger-springmvc and swagger models. 
See https://bintray.com/docs/uploads/uploads_publishing.html for more info on how to publish bintray releases.

- The version number is controlled by the version attribute in the top level build.gradle (e.g. `version = '0.7.80-SNAPSHOT`)


- Both released and snapshot versions are stored in jcenter's artifactory instance under oss-release-local and 
oss-snapshot-local respectively. You can browse the repos from here: https://oss.jfrog.org/webapp/browserepo.html