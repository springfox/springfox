package springfox.documentation.swagger.common

import org.springframework.http.HttpHeaders
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

import static java.util.Collections.*
import static springfox.documentation.swagger.common.HostNameProvider.*
import static springfox.documentation.swagger.common.XForwardPrefixPathAdjuster.*

class HostNameProviderSpec extends Specification {
  def "should prefix path with x-forwarded-prefix"() {
    given:
    def request = mockRequest(true)

    when:
    def result = componentsFrom(request, "/basePath")

    then:
    result.toUriString() == "http://localhost/prefix"
  }

  def "should preserve contextPath from request if no x-forwarded-prefix"() {
    given:
    def request = mockRequest(false)

    when:
    def result = componentsFrom(request, "/basePath")

    then:
    result.toUriString() == "http://localhost/contextPath"
  }

  def "should not be allowed to create object from utility class"() {
    when:
    new HostNameProvider()

    then:
    thrown UnsupportedOperationException
  }

  def mockRequest(boolean addXForwardedHeaders) {
    def request = Mock(HttpServletRequest.class)
    if (addXForwardedHeaders) {
      request.getHeader(X_FORWARDED_PREFIX) >> "/prefix"
      request.getHeaders(X_FORWARDED_PREFIX) >> headerValues()
    } else {
      request.getHeaders(X_FORWARDED_PREFIX) >> emptyEnumeration()
    }
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
