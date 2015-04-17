### Development Environment

- File >> open >> build.gradle
- Make sure to check the 'use the default gradle wrapper' option.
- First time build

```bash
./gradlew cleanIdea idea

```

- Code quality (code coverage, checkstyle)

```bash
./gradlew check
```
- To get more output from any gralde commands/tasks append a `-i` (info) or `-d` (debug) e.g.
```bash
./gradlew build -i

```

### Building the documentation 
```groovy
 ./gradlew asciidoc --daemon //Using the gradle daemon here makes sense if this is being executed a lot
```

### Releasing
- An illustrated version of the release process is described in the following issue: https://github.com/martypitt/swagger-springmvc/issues/422

### CI Enviroment
- https://circleci.com/gh/springfox/springfox