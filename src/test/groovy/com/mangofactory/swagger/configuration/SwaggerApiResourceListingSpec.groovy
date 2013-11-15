package com.mangofactory.swagger.configuration

import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiInfo
import com.wordnik.swagger.model.ApiKey
import com.wordnik.swagger.model.ApiListingReference
import com.wordnik.swagger.model.ResourceListing
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.swagger.ScalaUtils.fromList
import static com.mangofactory.swagger.ScalaUtils.fromOption

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class SwaggerApiResourceListingSpec extends Specification {
   String DEFAULT_RELATIVE_BASE_PATH = '/myApp'
   def ServletContext servletContext

   def setup() {
      def servletContext = Mock(ServletContext)
      servletContext.getContextPath() >> DEFAULT_RELATIVE_BASE_PATH
   }

   def "default swagger resource"() {
    when: "I create a swagger resource"
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(servletContext)
      swaggerApiResourceListing.createResourceListing()

    then: "I should should have the correct defaults"
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      def apiListingReferenceList = fromList(resourceListing.apis())
      def authorizationTypes = fromList(resourceListing.authorizations())

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
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(servletContext)
      swaggerApiResourceListing.apiInfo = apiInfo
      swaggerApiResourceListing.createResourceListing()

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
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(servletContext)
      swaggerApiResourceListing.authorizationTypes = [apiKey]
      swaggerApiResourceListing.createResourceListing()

    then:
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      def authorizationTypes = fromList(resourceListing.authorizations())
      def apiKeyAuthType = authorizationTypes[0]
      apiKeyAuthType instanceof ApiKey
      apiKeyAuthType.keyname == "api_key"
      apiKeyAuthType.passAs == "header"
   }

   def "resource with mocked apis"() {
    given:
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(servletContext)

      Map handlerMethods = [(requestMappingInfo("/somePath")): dummyHandlerMethod()]
      def requestHandlerMapping = Mock(RequestMappingHandlerMapping)
      requestHandlerMapping.getHandlerMethods() >> handlerMethods

      ApiListingReferenceScanner scanner = new ApiListingReferenceScanner([requestHandlerMapping],
              new DefaultControllerResourceGroupingStrategy())

      swaggerApiResourceListing.setApiListingReferenceScanner(scanner)

    when:
      swaggerApiResourceListing.createResourceListing()

    then:
      ResourceListing resourceListing = swaggerApiResourceListing.resourceListing
      ApiListingReference apiListingReference = resourceListing.apis().first()
      apiListingReference.path() == "/api-docs/somePath"
      apiListingReference.position() == 0
      fromOption(apiListingReference.description()) == "somePath"
   }
}
