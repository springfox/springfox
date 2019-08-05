package springfox.documentation.swagger.common

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.common.SpringVersion
import springfox.documentation.common.Version

import javax.servlet.http.HttpServletRequest


class XForwardPrefixPathAdjusterSpec extends Specification {

  def "Returns only prefix in x-forwarded-prefix"() {
    given:
    def request = Mock(HttpServletRequest.class)
    request.getHeader(XForwardPrefixPathAdjuster.X_FORWARDED_PREFIX) >> "/prefix"
    def springVersion = Mock(SpringVersion.class)
    springVersion.getVersion() >> Version.parse("4.3.15.RELEASE")

    when:
    def result = new XForwardPrefixPathAdjuster(request, springVersion).adjustedPath("/basePath")

    then:
    result == "/prefix"
  }

  def "Returns prefix in x-forwarded-prefix and path"() {
    given:
    def request = Mock(HttpServletRequest.class)
    request.getHeader(XForwardPrefixPathAdjuster.X_FORWARDED_PREFIX) >> "/prefix"
    def springVersion = Mock(SpringVersion.class)
    springVersion.getVersion() >> Version.parse("4.3.14.RELEASE")

    when:
    def result = new XForwardPrefixPathAdjuster(request, springVersion).adjustedPath("/basePath")

    then:
    result == "/prefix/basePath"
  }

  def "Returns only path"() {
    given:
    def request = Mock(HttpServletRequest.class)
    request.getHeader(XForwardPrefixPathAdjuster.X_FORWARDED_PREFIX) >> null

    when:
    def result = new XForwardPrefixPathAdjuster(request).adjustedPath("/basePath")

    then:
    result == "/basePath"
  }

  @Unroll
  def "When prefix is #prefix"() {
    given:
    def request = Mock(HttpServletRequest.class)
    request.getHeader(XForwardPrefixPathAdjuster.X_FORWARDED_PREFIX) >> prefix

    when:
    def result = new XForwardPrefixPathAdjuster(request).adjustedPath("/basePath")

    then:
    result == "/"

    where:
    prefix << ["", "/"]
  }
}
