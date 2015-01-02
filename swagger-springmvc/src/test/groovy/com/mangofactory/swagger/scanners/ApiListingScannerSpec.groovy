package com.mangofactory.swagger.scanners
import com.mangofactory.service.model.ApiListing
import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.core.ApiListingScanningContext
import com.mangofactory.swagger.mixins.ApiDescriptionSupport
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.ModelProviderForServiceSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.mangofactory.swagger.readers.ApiDescriptionReader
import com.mangofactory.swagger.readers.ApiModelReader
import com.mangofactory.swagger.readers.MediaTypeReader
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext

import static com.google.common.collect.Maps.*
import static com.mangofactory.swagger.scanners.ApiListingScanner.longestCommonPath
import static org.springframework.http.MediaType.*

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport, AuthSupport, ModelProviderForServiceSupport, ApiDescriptionSupport,
        SpringSwaggerConfigSupport])
class ApiListingScannerSpec extends Specification {
  def defaultValues
  DocumentationContextBuilder contextBuilder
  DocumentationContext context
  MediaTypeReader mediaTypeReader
  ApiDescriptionReader apiDescriptionReader
  ApiModelReader apiModelReader
  ApiListingScanningContext listingContext
  ApiListingScanner scanner

  def setup() {
    defaultValues = defaults(Mock(ServletContext))
    contextBuilder = new DocumentationContextBuilder(defaultValues).withHandlerMappings([])
    AuthorizationContext authorizationContext = AuthorizationContext.builder()
            .withAuthorizations(defaultAuth())
            .withIncludePatterns(['/anyPath.*'])
            .build()

    context = new SwaggerSpringMvcPlugin()
            .authorizationContext(authorizationContext)
            .build(contextBuilder)
    mediaTypeReader = Stub(MediaTypeReader)
    apiDescriptionReader = Stub(ApiDescriptionReader)
    apiModelReader = Mock(ApiModelReader)
    scanner = new ApiListingScanner(mediaTypeReader, apiDescriptionReader, apiModelReader)
  }

  def "Should create an api listing for a single resource grouping "() {
    given:
      RequestMappingInfo requestMappingInfo =
              requestMappingInfo("/businesses",
                      [
                              consumesRequestCondition: consumesRequestCondition(APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE),
                              producesRequestCondition: producesRequestCondition(APPLICATION_JSON_VALUE)
                      ]
              )

      RequestMappingContext requestMappingContext = new RequestMappingContext(context, requestMappingInfo,
              dummyHandlerMethod("methodWithConcreteResponseBody"))
      ResourceGroup resourceGroup = new ResourceGroup("businesses")
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(resourceGroup, [requestMappingContext])
      listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)
    when:
      mediaTypeReader.execute(requestMappingContext) >> {
        requestMappingContext.put("consumes", [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE])
        requestMappingContext.put("produces", [APPLICATION_JSON_VALUE])
      }
      apiDescriptionReader.execute(requestMappingContext) >> {
        requestMappingContext.put("apiDescriptionList", [])
      }
    and:
      def scanned = scanner.scan(listingContext)
    then:
      scanned.containsKey("businesses")
      ApiListing listing = scanned.get("businesses")
      listing.consumes ==[APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE]
      listing.produces ==[APPLICATION_JSON_VALUE]
  }

  def "should assign global authorizations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')
      RequestMappingContext requestMappingContext = new RequestMappingContext(context, requestMappingInfo,
              dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(new ResourceGroup("businesses"), [requestMappingContext])

      listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)
    and:
      apiDescriptionReader.execute(requestMappingContext) >> {
        requestMappingContext.put("apiDescriptionList", [])
      }
      mediaTypeReader.execute(requestMappingContext) >> {
        requestMappingContext.put("consumes", [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE])
        requestMappingContext.put("produces", [APPLICATION_JSON_VALUE])
      }
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
