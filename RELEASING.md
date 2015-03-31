### Releasing 

Set the projects semantic version as follows:
```groovy
SoftwareVersion currentVersion = BuildscriptVersionResolver.projectVersion(
        project, SemanticVersion.get(file("$rootDir/version.properties"))
)
```

```bash
./gradlew release -PbintrayUsername=<bintrayUsername> -PbintrayPassword=<bintrayPassword> -PreleaseType=<MAJOR|MINOR|PATCH>

```

### Snapshot
```groovy
SoftwareVersion currentVersion = BuildscriptVersionResolver.projectVersion(
        project, new SnapshotVersion(SemanticVersion.get(file("$rootDir/version.properties")))
)
```

```bash
./gradlew snapshot -PbintrayUsername=<bintrayUsername> -PbintrayPassword=<bintrayPassword>
```


### Override 
To bypass the standard release flow and upload directly to bintray use the following task
- manually set the version in version.properties
```bash
./gradlew clean build bintrayUpload -PbintrayUsername=<bintrayUsername> -PbintrayPassword=<bintrayPassword> -PreleaseType=MAJOR
 --stacktrace
```