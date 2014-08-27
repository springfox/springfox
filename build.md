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
- Coverage limits: see coverage.gradle `minCoverage = 80` 
- To get more output from any gralde commands/tasks append a `-i` (info) or `-d` (debug) e.g.
```bash
./gradlew build -i

```

### Releasing
- An illustrated version of the release process is described in the following issue: https://github.com/martypitt/swagger-springmvc/issues/422

### Jcenter repositories

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

### CI Enviroment

[TravicCI] (https://travis-ci.org/martypitt/swagger-springmvc)

Once build has passed and an artifact published, the [demo project] (https://github.com/adrianbk/swagger-springmvc-demo) will be triggered

#### Adding secure CI env variables to travisci
```
> gem install travis
> travis encrypt SOMEVAR=secretvalue

```
