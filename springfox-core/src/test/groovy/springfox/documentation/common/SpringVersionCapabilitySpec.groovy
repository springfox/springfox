package springfox.documentation.common

import spock.lang.Specification
import spock.lang.Unroll

import static springfox.documentation.common.SpringVersionCapability.*

class SpringVersionCapabilitySpec extends Specification {

  def "Cannot instantitiate SpringVersionCompatibility"() {
    when:
    new SpringVersionCapability()

    then:
    thrown UnsupportedOperationException
  }

  @Unroll
  def "Version #springVersion should preservePath #expected"() {
    when:
    def result = !supportsXForwardPrefixHeader(Version.parse(springVersion))

    then:
    result == expected

    where:
    springVersion     | expected
    "3.10.20.RELEASE" | true
    "4.2.20.RELEASE"  | true
    "4.3.14.RELEASE"  | true
    "4.3.15.RELEASE"  | false
    "4.4.16.RELEASE"  | false
    "5.0.0.RELEASE"   | true
    "5.0.5.RELEASE"   | false
    "5.1.0.RELEASE"   | false
    "5.1.5.RELEASE"   | false
    "6.1.6.RELEASE"   | false
  }

  @Unroll
  def "Version #springVersion should has extended PathVariable annotation #expected"() {
    when:
    def result = supportsExtendedPathVariableAnnotation(Version.parse(springVersion))

    then:
    result == expected

    where:
    springVersion     | expected
    "3.10.20.RELEASE" | false
    "4.2.20.RELEASE"  | false
    "4.3.2.RELEASE"   | false
    "4.3.3.RELEASE"   | true
    "4.3.14.RELEASE"  | true
    "4.3.15.RELEASE"  | true
    "4.4.16.RELEASE"  | true
    "5.0.0.RELEASE"   | true
    "5.0.5.RELEASE"   | true
    "5.1.0.RELEASE"   | true
    "5.1.5.RELEASE"   | true
    "6.1.6.RELEASE"   | true
  }
}
