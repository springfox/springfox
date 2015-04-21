### Releasing 

To release a non-snaphot version of Springfox:

1. Set the projects semantic version and Git push:
    ```groovy
    SoftwareVersion currentVersion = BuildscriptVersionResolver.projectVersion(
            project, SemanticVersion.get(file("$rootDir/version.properties"))
    )
```

2. Execute the the release commands:
The below properties are required to run a release:
    - `GITHUB_TOKEN`
    - `BINTRAY_USERNAME` 
    - `BINTRAY_PASSWORD`
    
    
    Recommend using [autoenv](https://github.com/kennethreitz/autoenv) with a `.env` file at the root of the repo. 

    ```bash
    ./gradlew release publishDocs -PbintrayUsername=$BINTRAY_USERNAME -PbintrayPassword=$BINTRAY_PASSWORD 
    -PreleaseType=<MAJOR|MINOR|PATCH> -i
    ```

3. Change the projects version back to snapshot and push:
```groovy
SoftwareVersion currentVersion = BuildscriptVersionResolver.projectVersion(
        project, new SnapshotVersion(SemanticVersion.get(file("$rootDir/version.properties")))
)
```

The release steps are as follows:
- check that the git workspace is clean
- check that the local git branch is master
- check that the local git branch is the same as origin
- gradle test
- gradle check
- upload (publish) all artifacts to Bintray
- Bumps the project version in `version.properties`
- Git tag the release
- Git push

### Snapshot
This is normally done by the CI server
```bash
./gradlew snapshot -PbintrayUsername=<bintrayUsername> -PbintrayPassword=<bintrayPassword>
```

### Override 
To bypass the standard release flow and upload directly to bintray use the following task
- manually set the version in version.properties
```bash
./gradlew clean build bintrayUpload -PbintrayUsername=$BINTRAY_USERNAME -PbintrayPassword=$BINTRAY_PASSWORD -PreleaseType=<MAJOR|MINOR|PATCH>
 --stacktrace
```

### Updating reference documantation.
To update the docs for an existing release pass the `updateMode` switch
```
./gradlew publishDocs -PupdateMode
```