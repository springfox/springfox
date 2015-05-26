package springfox.gradlebuild.version

import spock.lang.Specification
import spock.lang.Unroll

class GitVersionParserSpec extends Specification {

  @Unroll("Should gracefully not parse #version")
  def "Throws illegal args exception when version is in correct format" () {
    given:
      def sut = GitDescribeVersioningStrategy.create("<count>-Commits-<sha>-commitish") as GitVersionParser
    when:
      sut.parseTransform(version, "")
    then:
      thrown(IllegalArgumentException)
    where:
      version << ["1.0", "1", "1.a.0", "1.0.a", "1.0.0-SNAPSHOT", "1.0.0-a-g12a123", "1.0.0-1-g12a12z3"]
  }

  @Unroll("Transforms the versions correctly when transform is has count/sha #version")
  def "Transforms parsed versions correctly" () {
    given:
      def sut = GitDescribeVersioningStrategy.create("<count>-Commits-<sha>-commitish") as GitVersionParser
    when:
      def semver = sut.parseTransform(version, "<count>-Commits-<sha>-commitish")
    then:
      semver.major == major
      semver.minor == minor
      semver.patch == patch
      semver.buildSuffix == build
    where:
      version         | major | minor | patch | build
      "1.0.0"         | 1     | 0     | 0     | ""
      "1.0.0-1-gabdc" | 1     | 0     | 0     | "1-Commits-abdc-commitish"
  }


  @Unroll("Transforms the versions correctly when transform is empty #version")
  def "Transforms parsed versions correctly when transform suffix is empty" () {
    given:
      def sut = GitDescribeVersioningStrategy.create("")  as GitVersionParser
    when:
      def semver = sut.parseTransform(version, "")
    then:
      semver.major == major
      semver.minor == minor
      semver.patch == patch
      semver.buildSuffix == build
    where:
      version         | major | minor | patch | build
      "1.0.0"         | 1     | 0     | 0     | ""
      "1.0.0-1-gabdc" | 1     | 0     | 0     | ""
  }

}
