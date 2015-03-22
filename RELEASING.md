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
