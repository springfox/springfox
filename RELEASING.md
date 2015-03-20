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
TBD

