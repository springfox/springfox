package springdox.documentation.spring.web.scanners

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Unroll
import springdox.documentation.RequestMappingPatternMatcher
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.service.ApiListing
import springdox.documentation.service.ResourceGroup
import springdox.documentation.spi.service.contexts.AuthorizationContext
import springdox.documentation.spi.service.contexts.RequestMappingContext
import springdox.documentation.spring.web.dummy.DummyClass
import springdox.documentation.spring.web.mixins.ApiDescriptionSupport
import springdox.documentation.spring.web.mixins.AuthSupport
import springdox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

import static com.google.common.collect.Maps.*
import static com.google.common.collect.Sets.*
import static org.springframework.http.MediaType.*
import static springdox.documentation.spring.web.scanners.ApiListingScanner.*

@Mixin([RequestMappingSupport, AuthSupport, ModelProviderForServiceSupport,
        ServicePluginsSupport, ApiDescriptionSupport, SchemaPluginsSupport])
class ApiListingScannerSpec extends DocumentationContextSpec {
  ApiDescriptionReader apiDescriptionReader
  ApiModelReader apiModelReader
  ApiListingScanningContext listingContext
  ApiListingScanner scanner

  def setup() {
    AuthorizationContext authorizationContext = AuthorizationContext.builder()
            .withAuthorizations(defaultAuth())
            .withIncludePatterns(newHashSet('/anyPath.*'))
            .withRequestMappingPatternMatcher(Mock(RequestMappingPatternMatcher))
            .build()

    plugin
            .authorizationContext(authorizationContext)
            .configure(contextBuilder)
    apiDescriptionReader = Mock(ApiDescriptionReader)
    apiDescriptionReader.read(_) >> []
    apiModelReader = Mock(ApiModelReader)
    apiModelReader.read(_) >> newHashMap()
    scanner = new ApiListingScanner(apiDescriptionReader, apiModelReader, defaultWebPlugins())
  }

  def "Should create an api listing for a single resource grouping "() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/businesses")


      def context = context()
      RequestMappingContext requestMappingContext = new RequestMappingContext(context, requestMappingInfo,
              dummyHandlerMethod("methodWithConcreteResponseBody"))
      ResourceGroup resourceGroup = new ResourceGroup("businesses", DummyClass)
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(resourceGroup, [requestMappingContext])
      listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)
    when:
      apiDescriptionReader.read(requestMappingContext) >> []

    and:
      def scanned = scanner.scan(listingContext)
    then:
      scanned.containsKey("businesses")
      ApiListing listing = scanned.get("businesses")
      listing.consumes == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE] as Set
      listing.produces == [APPLICATION_JSON_VALUE] as Set
  }

  def "should assign global authorizations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')

      def context = context()
      RequestMappingContext requestMappingContext = new RequestMappingContext(context, requestMappingInfo,
              dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(new ResourceGroup("businesses", DummyClass), [requestMappingContext])

      listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)
    when:
      Map<String, ApiListing> apiListingMap = scanner.scan(listingContext)
    then:
      ApiListing listing = apiListingMap['businesses']
      listing.getAuthorizations().size() == 1
  }

  @Unroll
  def "should find longest common path"() {
    given:
      String result = longestCommonPath(apiDescriptions(paths))

    expect:
      result == expected
    where:
      paths                                        | expected
      []                                           | null
      ['/a/b', '/a/b']                             | '/a/b'
      ['/a/b', '/a/b/c']                           | '/a/b'
      ['/a/b', '/a/']                              | '/a'
      ['/a/b', '/a/d/e/f']                         | '/a'
      ['/a/b/c/d/e/f', '/a', '/a/b']               | '/a'
      ['/d', '/e', 'f']                            | '/'
      ['/a/b/c', '/a/b/c/d/e/f', '/a/b/c/d/e/f/g'] | '/a/b/c'
  }
}
