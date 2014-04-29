package com.mangofactory.swagger.configuration

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.core.DefaultSwaggerPathProvider
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.swagger.ScalaUtils.*

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class SwaggerApiResourceListingSpec extends Specification {

   def "assessors"() {
    given:
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(null, null)
      SwaggerCache cache = new SwaggerCache()
      swaggerApiResourceListing.setSwaggerCache(cache)
      List<AuthorizationType> authTypes = Arrays.asList(new ApiKey("", ""))
      swaggerApiResourceListing.setAuthorizationTypes(authTypes)
      DefaultSwaggerPathProvider provider = new DefaultSwaggerPathProvider()
      swaggerApiResourceListing.setSwaggerPathProvider(provider);
    expect:
      cache == swaggerApiResourceListing.getSwaggerCache()
      authTypes == swaggerApiResourceListing.getAuthorizationTypes()
      provider == swaggerApiResourceListing.getSwaggerPathProvider()
   }

   def "default swagger resource"() {
    when: "I create a swagger resource"
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
      swaggerApiResourceListing.initialize()

    then: "I should should have the correct defaults"
      ResourceListing resourceListing = swaggerCache.getResourceListing("default")
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
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
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
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
      swaggerApiResourceListing.authorizationTypes = [apiKey]
      swaggerApiResourceListing.initialize()

    then:
      ResourceListing resourceListing = swaggerCache.getResourceListing("default")
      def authorizationTypes = fromScalaList(resourceListing.authorizations())
      def apiKeyAuthType = authorizationTypes[0]
      apiKeyAuthType instanceof ApiKey
      apiKeyAuthType.keyname == "api_key"
      apiKeyAuthType.passAs == "header"
   }

   def "resource with mocked apis"() {
    given:
      SwaggerCache swaggerCache = new SwaggerCache();
      String swaggerGroup = "swaggerGroup"
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, swaggerGroup)
      ServletContext servletContext = [getContextPath: { return "/myApp" }] as ServletContext
      DefaultSwaggerPathProvider swaggerPathProvider = new DefaultSwaggerPathProvider(servletContext: servletContext)

      swaggerApiResourceListing.setSwaggerPathProvider(swaggerPathProvider)

     def settings = new SwaggerGlobalSettings()
     settings.setIgnorableParameterTypes(new SpringSwaggerConfig().defaultIgnorableParameterTypes())
     SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
     settings.alternateTypeProvider = springSwaggerConfig.defaultAlternateTypeProvider();
     swaggerApiResourceListing.setSwaggerGlobalSettings(settings)

    def resolver = new TypeResolver()
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(new ObjectMapper(), new AccessorsProvider(resolver),
            new FieldsProvider(resolver))
    def modelDependenciesProvider = new ModelDependencyProvider(resolver, modelPropertiesProvider)
    ModelProvider modelProvider = new DefaultModelProvider(resolver, modelPropertiesProvider,
            modelDependenciesProvider)

      Map handlerMethods = [(requestMappingInfo("somePath/")): dummyHandlerMethod()]
      def requestHandlerMapping = Mock(RequestMappingHandlerMapping)
      requestHandlerMapping.getHandlerMethods() >> handlerMethods

      ApiListingReferenceScanner scanner = new ApiListingReferenceScanner()
      scanner.setRequestMappingHandlerMapping([requestHandlerMapping])
      scanner.setResourceGroupingStrategy(new ClassOrApiAnnotationResourceGrouping())
      scanner.setSwaggerGroup("swaggerGroup")

      scanner.setSwaggerPathProvider(swaggerPathProvider)
      swaggerApiResourceListing.setModelProvider(modelProvider)
      swaggerApiResourceListing.setApiListingReferenceScanner(scanner)

    when:
      swaggerApiResourceListing.initialize()
      ResourceListing resourceListing = swaggerCache.getResourceListing("swaggerGroup")
    then:

      ApiListingReference apiListingReference = resourceListing.apis().head()
      apiListingReference.path() == "/swaggerGroup/com_mangofactory_swagger_dummy_DummyClass"
      apiListingReference.position() == 0
      fromOption(apiListingReference.description()) == "com.mangofactory.swagger.dummy.DummyClass"

    and:
      ApiListing apiListing =
              swaggerCache.swaggerApiListingMap['swaggerGroup']['com_mangofactory_swagger_dummy_DummyClass']
      apiListing.swaggerVersion() == '1.2'
      apiListing.basePath() == 'http://127.0.0.1:8080/myApp'
      apiListing.resourcePath() == '/com_mangofactory_swagger_dummy_DummyClass'

   }
}
