package springfox.documentation.swagger2.web

import com.google.common.collect.LinkedListMultimap
import org.springframework.web.util.WebUtils
import spock.lang.Unroll
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule
import springfox.documentation.swagger2.mappers.MapperSupport

import javax.servlet.http.HttpServletRequest

import static com.google.common.collect.Maps.*

@Mixin([ApiListingSupport, AuthSupport])
class Swagger2ControllerSpec extends DocumentationContextSpec
    implements MapperSupport, JsonSupport{

  Swagger2Controller controller = new Swagger2Controller(
          new DocumentationCache(),
          swagger2Mapper(),
          new JsonSerializer([new Swagger2JacksonModule()]))

  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner
  HttpServletRequest request

  def setup() {
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(newHashMap())
    listingScanner = Mock(ApiListingScanner)
    listingScanner.scan(_) >> LinkedListMultimap.create()

    request = servletRequest()
  }


  @Unroll
  def "should return #expectedStatus for group #group"() {
    given:
      ApiDocumentationScanner scanner =
          new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(scanner.scan(context()))
    when:
      def result = controller.getDocumentation(group, request)
    then:
      result.getStatusCode().value() == expectedStatus
    where:
      group     | expectedStatus
      null       | 200
      "default" | 200
      "unknown" | 404
  }

  @Unroll("x-forwarded-prefix: #prefix")
  def "should respect proxy headers ('X-Forwarded-*') when setting host, port and basePath"() {
    given:
      ApiDocumentationScanner swaggerApiResourceListing =
          new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(context()))
    and:
      def req = servletRequestWithXHeaders(prefix)
    when:
      def result = jsonBodyResponse(controller.getDocumentation(null, req).getBody().value())

    then:
      result.basePath == expectedPath

    where:
      prefix        | expectedPath
      "/fooservice" | "/fooservice"
      "/"           | "/"
      ""            | "/"
  }

  def "Should omit port number if it is -1"() {
    given:
      ApiDocumentationScanner swaggerApiResourceListing =
        new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
      controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(context()))
    and:
      controller.hostNameOverride = "DEFAULT"
    when:
      def result = controller.getDocumentation(null, request)
    and:
      //Need to find out why jsonPath mvc result matcher doesn't work
      def slurped = jsonBodyResponse(result.getBody().value())
    then:
      slurped.host == "localhost"
      result.getStatusCode().value() == 200
  }


  def servletRequest() {
    HttpServletRequest request = Mock(HttpServletRequest)
    request.contextPath >> ""
    request.servletPath >> ""
    request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080/api-docs"
    request.requestURL >> new StringBuffer("http://localhost/api-docs")
    request.headerNames >> Collections.enumeration([])
    request
  }

  def servletRequestWithXHeaders(prefix) {
    HttpServletRequest request = Mock(HttpServletRequest)
    request.contextPath >> ""
    request.servletPath >> ""
    request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080/api-docs"
    request.requestURL >> new StringBuffer("http://localhost/api-docs")
    request.headerNames >>> [Collections.enumeration(["X-Forwarded-Host", "X-Forwarded-Prefix"]),
                             Collections.enumeration(["X-Forwarded-Host", "X-Forwarded-Prefix"])]
    request.getHeader("X-Forwarded-Host") >> "myhost:6060"
    request.getHeader("X-Forwarded-Prefix") >> prefix
    request.getHeaders("X-Forwarded-Host") >> Collections.enumeration(["myhost:6060"])
    request.getHeaders("X-Forwarded-Prefix") >> Collections.enumeration([prefix])
    request
  }
}