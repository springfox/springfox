package com.mangofactory.swagger.scanners

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.ApiDescriptionSupport
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiDescription
import com.wordnik.swagger.model.ApiListing
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.common.collect.Maps.newHashMap
import static com.mangofactory.swagger.ScalaUtils.fromOption
import static com.mangofactory.swagger.ScalaUtils.fromScalaList
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
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, absoluteSwaggerPathProvider(),
              modelProvider(), null, [])

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
      listing.swaggerVersion() == SwaggerSpec.version()
      listing.apiVersion() == "1.0"
      listing.basePath() == "http://localhost:8080/context-path"
      listing.resourcePath() == "/api/v1/businesses"
      listing.position() == 0
      fromScalaList(listing.consumes()) == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE]
      fromScalaList(listing.produces()) == [APPLICATION_JSON_VALUE]
      ApiDescription apiDescription = fromScalaList(listing.apis())[0]
      apiDescription.path() == '/api/v1/businesses'
      fromOption(apiDescription.description()) == "methodWithConcreteResponseBody"
      def models = apiDescription.operations().head()

  }

  def "should assign global authorizations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')
      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = newHashMap()
      resourceGroupRequestMappings.put(new ResourceGroup("businesses"), [requestMappingContext])
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, absoluteSwaggerPathProvider(),
              modelProvider(), null, [])
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
      listing.authorizations().size() == 1
  }

  @Unroll
  def "should find longest common path"() {
    given:
      ApiListingScanner apiListingScanner = new ApiListingScanner(null, null, null, null, null)

    when:
      String result = apiListingScanner.longestCommonPath(apiDescriptions(paths))

    then:
      result == expected
    where:
      paths                                                            | expected
      []                                                               | null
      ['/a/b', '/a/b']                                                 | '/a/b'
      ['/a/b', '/a/b/c']                                               | '/a/b'
      ['/a/b', '/a/']                                                  | '/a'
      ['/a/b', '/a/d/e/f']                                             | '/a'
      ['/a/b/c/d/e/f', '/a', '/a/b']                                   | '/a'
      ['/d', '/e', 'f']                                                | '/'
      ['/a/b/c', '/a/b/c/d/e/f', '/a/b/c/d/e/f/g']                     | '/a/b/c'
  }
}
