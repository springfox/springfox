package com.mangofactory.swagger.scanners

import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SwaggerPathProviderSupport
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiDescription
import com.wordnik.swagger.model.ApiListing
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromOption
import static com.mangofactory.swagger.ScalaUtils.fromScalaList
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE

@Mixin([RequestMappingSupport, SwaggerPathProviderSupport, AuthSupport])
class ApiListingScannerSpec extends Specification {

   final ClassOrApiAnnotationResourceGrouping controllerNamingStrategy = new ClassOrApiAnnotationResourceGrouping()

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
      Map resourceGroupRequestMappings = ['businesses': [requestMappingContext]]
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, "default", swaggerPathProvider(), null)
      scanner.setSwaggerGlobalSettings(new SwaggerGlobalSettings())

    when:
      Map<String, ApiListing> apiListingMap = scanner.scan()
    then:
      apiListingMap.size() == 1

      ApiListing listing = apiListingMap['businesses']
      listing.swaggerVersion() == SwaggerSpec.version()
      listing.apiVersion() == "1.0"
      listing.basePath() == "http://127.0.0.1:8080/context-path"
      listing.resourcePath() == "/api/v1/businesses"
      listing.position() == 0
      fromScalaList(listing.consumes()) == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE]
      fromScalaList(listing.produces()) == [APPLICATION_JSON_VALUE]
      ApiDescription apiDescription = fromScalaList(listing.apis())[0]
      apiDescription.path() == '/api/v1/businesses'
      fromOption(apiDescription.description()) == "methodWithConcreteResponseBody"
   }

   def "should assign global authorizations"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')
      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, dummyHandlerMethod("methodWithConcreteResponseBody"))
      Map resourceGroupRequestMappings = ['businesses': [requestMappingContext]]
      ApiListingScanner scanner = new ApiListingScanner(resourceGroupRequestMappings, "default", swaggerPathProvider(), null)
      scanner.setSwaggerGlobalSettings(new SwaggerGlobalSettings())

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
}
