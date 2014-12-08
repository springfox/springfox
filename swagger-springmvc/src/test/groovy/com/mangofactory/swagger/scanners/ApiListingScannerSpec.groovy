package com.mangofactory.swagger.scanners

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.RequestMappingEvaluator
import com.mangofactory.swagger.mixins.*
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.models.dto.ApiDescription
import com.mangofactory.swagger.models.dto.ApiListing
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.common.collect.Lists.newArrayList
import static com.google.common.collect.Maps.newHashMap
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport, AuthSupport, ModelProviderSupport, ApiDescriptionSupport])
class ApiListingScannerSpec extends Specification {

  def "Should create an api listing for a single resource grouping "() {
    given:
      RequestMappingInfo requestMappingInfo =
              requestMappingInfo("/businesses",
                      [
                              consumesRequestCondition: consumesRequestCondition(APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE),
                              producesRequestCondition: producesRequestCondition(APPLICATION_JSON_VALUE)
                      ]
              )

      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(new ResourceGroup("businesses"), [requestMappingContext])
      RequestMappingEvaluator evaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, absoluteSwaggerPathProvider(),
              modelProvider(), null, [], evaluator)

      def settings = new SwaggerGlobalSettings()
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = springSwaggerConfig.defaultIgnorableParameterTypes()
      SwaggerModelsConfiguration modelsConfiguration = new SwaggerModelsConfiguration()
      settings.alternateTypeProvider = modelsConfiguration.alternateTypeProvider(new TypeResolver())
      scanner.setSwaggerGlobalSettings(settings)

    when:
      Map<String, ApiListing> apiListingMap = scanner.scan()
    then:
      apiListingMap.size() == 1

      ApiListing listing = apiListingMap['businesses']
      listing.getSwaggerVersion() == "1.2"
      listing.getApiVersion() == "1.0"
      listing.getBasePath() == "http://localhost:8080/context-path"
      listing.getResourcePath() == "/api/v1/businesses"
      listing.getPosition() == 0
      listing.getConsumes() == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE]
      listing.getProduces() == [APPLICATION_JSON_VALUE]
      ApiDescription apiDescription = listing.getApis()[0]
      apiDescription.getPath() == '/api/v1/businesses'
      apiDescription.getDescription() == "methodWithConcreteResponseBody"
      def models = apiDescription.getOperations().head()

  }

  def "should assign global authorizations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')
      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(new ResourceGroup("businesses"), [requestMappingContext])
      RequestMappingEvaluator evaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, absoluteSwaggerPathProvider(),
              modelProvider(), null, [], evaluator)
      def settings = new SwaggerGlobalSettings()
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      settings.ignorableParameterTypes = springSwaggerConfig.defaultIgnorableParameterTypes()
      SwaggerModelsConfiguration modelsConfiguration = new SwaggerModelsConfiguration()
      settings.alternateTypeProvider = modelsConfiguration.alternateTypeProvider(new TypeResolver());
      scanner.setSwaggerGlobalSettings(settings)

      AuthorizationContext authorizationContext = new AuthorizationContext.AuthorizationContextBuilder(defaultAuth())
              .withIncludePatterns(['/anyPath.*'])
              .build()

      scanner.setAuthorizationContext(authorizationContext)

    when:
      Map<String, ApiListing> apiListingMap = scanner.scan()

    then:
      ApiListing listing = apiListingMap['businesses']
      listing.getAuthorizations().size() == 1
  }

  @Unroll
  def "should find longest common path"() {
    given:
      RequestMappingEvaluator evaluator = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(".*?"))
      ApiListingScanner apiListingScanner = new ApiListingScanner(null, null, null, null, null, evaluator)

    when:
      String result = apiListingScanner.longestCommonPath(apiDescriptions(paths))

    then:
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
