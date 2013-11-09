package com.mangofactory.swagger.configuration

import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiInfo
import com.wordnik.swagger.model.ApiKey
import com.wordnik.swagger.model.AuthorizationType
import com.wordnik.swagger.model.ResourceListing
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.swagger.ScalaUtils.fromList
import static com.mangofactory.swagger.ScalaUtils.fromOption

class SwaggerApiResourceListingSpec extends Specification {
   String DEFAULT_RELATIVE_BASE_PATH = '/myApp'
   def WebApplicationContext webApplicationContext

   def setup() {
      webApplicationContext = Mock()
      def servletContext = Mock(ServletContext)
      servletContext.getContextPath() >> DEFAULT_RELATIVE_BASE_PATH
      webApplicationContext.getServletContext() >> Mock(ServletContext)
      webApplicationContext.getServletContext() >> servletContext
   }

   def "default swagger resource"() {
    when: "I create a swagger resource"
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(webApplicationContext)
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
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(webApplicationContext)
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
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(webApplicationContext)
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
}
