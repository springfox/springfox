### Development Environment

- File >> open >> build.gradle
- Make sure to check the 'use the default gradle wrapper' option.
- First time build

```bash
./gradlew cleanIdea idea

```

- To get more output from any gralde commands/tasks append a `-i` (info) or `-d` (debug) e.g.
```bash
./gradlew build -i

```

### Pre-Commit Build
- Code quality (code coverage, checkstyle)

```bash
./gradlew check
```

### Building the asciidoc reference documentation 
```groovy
 ./gradlew asciidoc --daemon //Using the gradle daemon here makes sense if this is being executed a lot
```

### CI Enviroment
- https://circleci.com/gh/springfox/springfox