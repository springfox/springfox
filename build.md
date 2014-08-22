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
- To get more output from any gralde commands/tasks append a `-i` (info) or `-d` (debug)

### Releasing
- An illistrated version of the release process is described in the following issue: https://github.com/martypitt/swagger-springmvc/issues/422

- The following command will publish the artifacts to bintray/jcenter. Only 'release' versions are hosted on bintray. 
This command will fail if you try to publish a release version that has already been published.

```bash
./gradlew -Pusername=<bintrayusername> -Ppassword=<bintraytoken> publish
```

- The version number is controlled by the version attribute in the top level build.gradle (e.g. `version = '0.7.80-SNAPSHOT`)

- Both released and snapshot versions are stored in jcenter's artifactory instance under oss-release-local and 
oss-snapshot-local respectively. You can browse the repos from here: https://oss.jfrog.org/webapp/browserepo.html

- Gradle
```groovy

repositories {
  jcenter()
  maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local/' }

}
 
compile(group: 'com.mangofactory', name: 'swagger-models', version: '0.7.80')

```

- Maven

```xml

<distributionManagement>
    <repository>
        <id>jcenter/id>
        <name>jcenter</name>
        <url>http://jcenter.bintray.com/</url>
    </repository>
    <repository>
        <id>jfrog-snapshots</id>
        <name>jfrog-snapshots</name>
        <url>http://oss.jfrog.org/artifactory/oss-snapshot-local/</url>
    </repository>
</distributionManagement>

<dependency>
    <groupId>com.mangofactory</groupId>
    <artifactId>swagger-models</artifactId>
    <version>version-RELEASE</version>
</dependency>

<dependency>
    <groupId>com.mangofactory</groupId>
    <artifactId>swagger-models</artifactId>
    <version>version-SNAPSHOT</version>
</dependency>

```