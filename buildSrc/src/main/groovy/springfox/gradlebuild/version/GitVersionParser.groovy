package springfox.gradlebuild.version

import java.util.stream.Stream
import java.util.stream.StreamSupport

import static java.util.Optional.*
import static java.util.stream.Collectors.*

// Lifted from plugin 'com.cinnober.gradle:semver-git:2.2.0'
// https://github.com/cinnober/semver-git
trait GitVersionParser {

  def patchComponents(String versionPart) {
    def pattern = /^([0-9]+)(-([0-9]+)-g([0-9a-f]+))?$/
    def matcher = versionPart =~ pattern
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Not a valid version. Expecting a version of form <MAJOR.MINOR.PATCH> where " +
          "e.g. 1.0.0-SNAPSHOT, 1.0.0-1-g10a2eg: $versionPart")
    }
    def patchComponents = matcher.collect { it }[0]
    if (patchComponents.size() < 4) {
      throw new IllegalArgumentException("Not a valid version. Expecting a version of form <MAJOR.MINOR.PATCH> where " +
          "e.g. 1.0.0-SNAPSHOT, 1.0.0-1-g10a2eg: $versionPart")
    }
    Integer patch = patchComponents[1].toInteger()
    Integer count = ofNullable(patchComponents[3]).orElse("0").toInteger()
    String sha = patchComponents[4]
    String build = patchComponents[2]?.substring(1)
    [patch, build, count, sha]
  }

  SemanticVersion parseTransform(String version, String buildSuffix) {
    def components = Stream.of(version.split("\\.")).collect(toList())
    if (StreamSupport.stream(components.spliterator(), false).count() < 3) {
      throw new IllegalArgumentException("Not a valid version. Expecting a version of form <MAJOR.MINOR.PATCH> where " +
          "e.g. 1.0.0-SNAPSHOT, 1.0.0-1-g10a2eg: ${version}")
    }
    def versions = components.iterator()
    Integer major = versions.next().toInteger()
    Integer minor = versions.next().toInteger()
    def (Integer patch, String build, Integer count, String sha) = patchComponents(versions.next())
    SemanticVersion parsedVersion = new SemanticVersion(major, minor, patch, "")
    String suffix = buildSuffix;
    if (count == 0) {
      suffix = ""
    } else {
      suffix = suffix.replaceAll("<count>", "$count")
      suffix = suffix.replaceAll("<sha>", ofNullable(sha).orElse(""))
    }
    return new SemanticVersion(parsedVersion.major, parsedVersion.minor, patch, suffix)
  }


}
