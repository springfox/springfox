package com.mangofactory.swagger.configuration

import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.DefaultSwaggerPathProvider
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiInfo
import com.wordnik.swagger.model.ApiKey
import com.wordnik.swagger.model.ApiListingReference
import com.wordnik.swagger.model.ResourceListing
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromOption
import static com.mangofactory.swagger.ScalaUtils.fromScalaList

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class SwaggerApiResourceListingSpec extends Specification {

   def "default swagger resource"() {
    when: "I create a swagger resource"
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing()
      swaggerApiResourceListing.initialize()

    then: "I should should have the correct defaults"
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      def apiListingReferenceList = fromScalaList(resourceListing.apis())
      def authorizationTypes = fromScalaList(resourceListing.authorizations())

      resourceListing.apiVersion() == "1"
      resourceListing.swaggerVersion() == SwaggerSpec.version()

      fromOption(resourceListing.info()) == null
      apiListingReferenceList == []
      authorizationTypes == []
   }

   def "resource with api info"() {
    given:
      ApiInfo apiInfo = new ApiInfo("title", "description", "terms", "contact", "license", "licenseUrl")
    when:
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing()
      swaggerApiResourceListing.apiInfo = apiInfo
      swaggerApiResourceListing.initialize()

    then:
      swaggerApiResourceListing.apiInfo.title() == "title"
      swaggerApiResourceListing.apiInfo.description() == "description"
      swaggerApiResourceListing.apiInfo.termsOfServiceUrl() == "terms"
      swaggerApiResourceListing.apiInfo.contact() == "contact"
      swaggerApiResourceListing.apiInfo.license() == "license"
      swaggerApiResourceListing.apiInfo.licenseUrl() == "licenseUrl"
   }

   def "resource with authorization types"() {
    given:
      ApiKey apiKey = new ApiKey("api_key", "header")
    when:
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing()
      swaggerApiResourceListing.authorizationTypes = [apiKey]
      swaggerApiResourceListing.initialize()

    then:
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      def authorizationTypes = fromScalaList(resourceListing.authorizations())
      def apiKeyAuthType = authorizationTypes[0]
      apiKeyAuthType instanceof ApiKey
      apiKeyAuthType.keyname == "api_key"
      apiKeyAuthType.passAs == "header"
   }

   def "resource with mocked apis"() {
    given:
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing()
      DefaultSwaggerPathProvider swaggerPathProvider = Stub()

      swaggerApiResourceListing.setSwaggerPathProvider(swaggerPathProvider)

      Map handlerMethods = [(requestMappingInfo("/somePath")): dummyHandlerMethod()]
      def requestHandlerMapping = Mock(RequestMappingHandlerMapping)
      requestHandlerMapping.getHandlerMethods() >> handlerMethods

      ApiListingReferenceScanner scanner = new ApiListingReferenceScanner([requestHandlerMapping],
              new DefaultControllerResourceGroupingStrategy())

      swaggerApiResourceListing.setApiListingReferenceScanner(scanner)

    when:
      swaggerApiResourceListing.initialize()

    then:
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      ApiListingReference apiListingReference = resourceListing.apis().first()
      apiListingReference.path() == "/default/somePath"
      apiListingReference.position() == 0
      fromOption(apiListingReference.description()) == "somePath"
   }
}
