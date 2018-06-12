package springfox.documentation.swagger.common

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest


class XForwardPrefixPathAdjusterSpec extends Specification {

  def "should prefix path with x-forwarded-prefix"() {
    given:
    def request = Mock(HttpServletRequest.class)
    request.getHeader(XForwardPrefixPathAdjuster.X_FORWARDED_PREFIX) >> "/prefix"

    when:
    def result = new XForwardPrefixPathAdjuster(request).adjustedPath("/basePath")

    then:
    result == "/prefix"
  }
}
