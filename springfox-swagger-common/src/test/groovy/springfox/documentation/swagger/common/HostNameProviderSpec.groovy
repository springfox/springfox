package springfox.documentation.swagger.common

import org.springframework.http.HttpHeaders
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

import static springfox.documentation.swagger.common.HostNameProvider.*
import static springfox.documentation.swagger.common.XForwardPrefixPathAdjuster.*

class HostNameProviderSpec extends Specification {
  def "should prefix path with x-forwarded-prefix"() {
    given:
    def request = mockRequest()

    when:
    def result = componentsFrom(request, "/basePath")

    then:
    result.toUriString() == "http://localhost/prefix"
  }

  def "should not be allowed to create object from utility class"() {
    when:
    new HostNameProvider()

    then:
    thrown UnsupportedOperationException
  }

  def mockRequest() {
    def request = Mock(HttpServletRequest.class)
    request.getHeader(X_FORWARDED_PREFIX) >> "/prefix"
    request.getHeaders(X_FORWARDED_PREFIX) >> headerValues()
    request.headerNames >> headerNames()
    request.requestURL >> new StringBuffer("http://localhost/contextPath")
    request.requestURI >> new URI("http://localhost/contextPath")
    request.contextPath >> "/contextPath"
    request.servletPath >> ""
    request
  }

  Enumeration<String> headerNames() {
    def headers = new ArrayList<String>()
    headers.add(X_FORWARDED_PREFIX)
    Collections.enumeration(headers)
  }

  Enumeration<String> headerValues() {
    def headerValues = new ArrayList<String>()
    headerValues.add("/prefix")
    Collections.enumeration(headerValues)
  }

  def headers() {
    def headers = new HttpHeaders()
    headers.add(X_FORWARDED_PREFIX, "/prefix")
    headers
  }

}
