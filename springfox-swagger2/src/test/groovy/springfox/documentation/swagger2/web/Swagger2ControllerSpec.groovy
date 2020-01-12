package springfox.documentation.swagger2.web

import com.fasterxml.classmate.TypeResolver
import org.springframework.mock.env.MockEnvironment
import org.springframework.web.util.WebUtils
import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.json.JsonSerializer
import springfox.documentation.spring.web.mixins.ApiListingSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.JsonSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.DefaultConfiguration
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanResult
import springfox.documentation.spring.web.scanners.ApiListingReferenceScanner
import springfox.documentation.spring.web.scanners.ApiListingScanner
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule
import springfox.documentation.swagger2.mappers.MapperSupport

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

import static java.util.Collections.*
import static springfox.documentation.spi.service.contexts.Orderings.*

class Swagger2ControllerSpec extends DocumentationContextSpec
    implements MapperSupport,
        JsonSupport,
        AuthSupport,
        ApiListingSupport {

  Swagger2ControllerWebMvc controller = new Swagger2ControllerWebMvc(
      mockEnvironment(),
      new DocumentationCache(),
      swagger2Mapper(),
      new JsonSerializer([new Swagger2JacksonModule()]))

  ApiListingReferenceScanner listingReferenceScanner
  ApiListingScanner listingScanner
  HttpServletRequest request

  def setup() {
    listingReferenceScanner = Mock(ApiListingReferenceScanner)
    listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult(new HashMap<>())
    listingScanner = Mock(ApiListingScanner)
    listingScanner.scan(_) >> new HashMap<>()

    request = servletRequest()
  }

  def mockEnvironment() {
    def environment = new MockEnvironment()
    environment.withProperty("springfox.documentation.swagger.v1.path", "/v1")
    environment.withProperty("springfox.documentation.swagger.v2.path", "/v2")
    environment
  }


  @Unroll
  def "should return #expectedStatus for group #group"() {
    given:
    ApiDocumentationScanner scanner =
        new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
    controller.documentationCache.addDocumentation(scanner.scan(documentationContext()))

    when:
    def result = controller.getDocumentation(group, request)

    then:
    result.getStatusCode().value() == expectedStatus

    where:
    group     | expectedStatus
    null      | 200
    "default" | 200
    "unknown" | 404
  }

  @Unroll("x-forwarded-prefix: #prefix")
  def "should respect proxy headers ('X-Forwarded-*') when setting host, port and basePath"() {
    given:
    def req = servletRequestWithXHeaders(prefix)

    def defaultConfiguration = new DefaultConfiguration(
        new Defaults(),
        new TypeResolver(),
        new DefaultPathProvider())
    
    this.contextBuilder = defaultConfiguration.create(DocumentationType.SWAGGER_12)
        .requestHandlers([])
        .operationOrdering(nickNameComparator())

    ApiDocumentationScanner swaggerApiResourceListing =
        new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
    controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(documentationContext()))

    when:
    def result = jsonBodyResponse(controller.getDocumentation(null, req).getBody().value())

    then:
    result.basePath == expectedPath

    where:
    prefix        | expectedPath
    "/fooservice" | "/fooservice/servletPath"
    "/"           | "/contextPath/servletPath"
    ""            | "/contextPath/servletPath"
  }

  def "Should omit port number if it is -1"() {
    given:
    ApiDocumentationScanner swaggerApiResourceListing =
        new ApiDocumentationScanner(listingReferenceScanner, listingScanner)
    controller.documentationCache.addDocumentation(swaggerApiResourceListing.scan(documentationContext()))

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
    def contextPath = "/contextPath"

    HttpServletRequest request = Mock(HttpServletRequest)
    request.contextPath >> contextPath
    request.servletPath >> "/servletPath"
    request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080/api-docs"
    request.requestURL >> new StringBuffer("http://localhost/api-docs")
    request.headerNames >> enumeration([])
    request.servletContext >> servletContext(contextPath)

    request
  }

  def servletRequestWithXHeaders(prefix) {
    def contextPath = "/contextPath"

    HttpServletRequest request = Mock(HttpServletRequest)
    request.contextPath >> contextPath
    request.servletPath >> "/servletPath"
    request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) >> "http://localhost:8080/api-docs"
    request.requestURL >> new StringBuffer("http://localhost/api-docs")
    request.headerNames >>> [enumeration(["X-Forwarded-Host", "X-Forwarded-Prefix"]),
                             enumeration(["X-Forwarded-Host", "X-Forwarded-Prefix"])]
    request.getHeader("X-Forwarded-Host") >> "myhost:6060"
    request.getHeader("X-Forwarded-Prefix") >> prefix
    request.getHeaders("X-Forwarded-Host") >> enumeration(["myhost:6060"])
    request.getHeaders("X-Forwarded-Prefix") >> enumeration([prefix])
    request.servletContext >> servletContext(contextPath)

    request
  }

  def servletContext(String contextPath) {
    ServletContext servletContext = Mock(ServletContext)
    servletContext.contextPath >> contextPath

    servletContext
  }
}