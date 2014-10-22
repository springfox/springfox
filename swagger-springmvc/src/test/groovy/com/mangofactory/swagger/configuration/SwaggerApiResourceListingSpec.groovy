package com.mangofactory.swagger.configuration

import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.wordnik.swagger.models.Contact
import com.wordnik.swagger.models.Info
import com.wordnik.swagger.models.License
import com.wordnik.swagger.models.Swagger
import spock.lang.Specification

@Mixin([RequestMappingSupport, SpringSwaggerConfigSupport])
class SwaggerApiResourceListingSpec extends Specification {

  def "assessors"() {
    given:
      SwaggerCache cache = new SwaggerCache()
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(cache, null)
      AbsoluteSwaggerPathProvider provider = new AbsoluteSwaggerPathProvider()
      swaggerApiResourceListing.setSwaggerPathProvider(provider);
    expect:
      cache == swaggerApiResourceListing.getSwaggerCache()
      provider == swaggerApiResourceListing.getSwaggerPathProvider()
  }

  def "default swagger resource"() {
    when: "I create a swagger resource"
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
      swaggerApiResourceListing.initialize()

    then: "I should should have the correct defaults"
      Swagger swagger = swaggerCache.getSwagger("default")
      swagger.swagger == "2.0"
  }

  def "resource with api info"() {
    given:
      Info apiInfo = new Info(
              title: "title",
              description: "description",
              termsOfService: "terms",
              contact: new Contact(name: 'cname', url: 'curl', email: 'cemail'),
              license: new License(name: 'lname', url: 'lurl')
      )

    when:
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
      swaggerApiResourceListing.apiInfo = apiInfo
      swaggerApiResourceListing.initialize()

    then:
      swaggerApiResourceListing.apiInfo.getTitle() == "title"
      swaggerApiResourceListing.apiInfo.getDescription() == "description"
      swaggerApiResourceListing.apiInfo.getTermsOfService() == "terms"

    and:
      swaggerApiResourceListing.apiInfo.contact.name == "cname"
      swaggerApiResourceListing.apiInfo.contact.url == "curl"
      swaggerApiResourceListing.apiInfo.contact.email == "cemail"

    and:
      swaggerApiResourceListing.apiInfo.license.name == "lname"
      swaggerApiResourceListing.apiInfo.license.url == "lurl"
  }
//
//  def "resource with authorization types"() {
//    given:
//      ApiKey apiKey = new ApiKey("api_key", "header")
//    when:
//      SwaggerCache swaggerCache = new SwaggerCache();
//      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
//      swaggerApiResourceListing.authorizationTypes = [apiKey]
//      swaggerApiResourceListing.initialize()
//
//    then:
//      ResourceListing resourceListing = swaggerCache.getSwagger("default")
//      def authorizationTypes = fromScalaList(resourceListing.authorizations())
//      def apiKeyAuthType = authorizationTypes[0]
//      apiKeyAuthType instanceof ApiKey
//      apiKeyAuthType.keyname == "api_key"
//      apiKeyAuthType.passAs == "header"
//  }
//
//  def "resource with mocked apis"() {
//    given:
//      SwaggerCache swaggerCache = new SwaggerCache();
//      String swaggerGroup = "swaggerGroup"
//      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, swaggerGroup)
//
//      SwaggerPathProvider swaggerPathProvider = new AbsoluteSwaggerPathProvider(servletContext: servletContext())
//      swaggerApiResourceListing.setSwaggerPathProvider(swaggerPathProvider)
//
//      def settings = new SwaggerGlobalSettings()
//      settings.setIgnorableParameterTypes(new SpringSwaggerConfig().defaultIgnorableParameterTypes())
//      SpringSwaggerConfig springSwaggerConfig = springSwaggerConfig()
//      settings.alternateTypeProvider = springSwaggerConfig.defaultAlternateTypeProvider();
//      swaggerApiResourceListing.setSwaggerGlobalSettings(settings)
//
//      def resolver = new TypeResolver()
//      def objectMapper = new ObjectMapper()
//      def fields = new FieldProvider(resolver)
//      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)
//
//      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(resolver), resolver,
//              settings.alternateTypeProvider, namingStrategy)
//      def fieldModelPropertyProvider =
//              new FieldModelPropertyProvider(fields, settings.alternateTypeProvider, namingStrategy)
//      def constructorModelPropertyProvider = new ConstructorModelPropertyProvider(fields,
//              settings.alternateTypeProvider, namingStrategy)
//
//      def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
//              fieldModelPropertyProvider, constructorModelPropertyProvider)
//      modelPropertiesProvider.objectMapper = objectMapper
//      def modelDependenciesProvider = new ModelDependencyProvider(resolver, settings.alternateTypeProvider,
//              modelPropertiesProvider)
//      ModelProvider modelProvider = new DefaultModelProvider(resolver, settings.alternateTypeProvider,
//              modelPropertiesProvider,
//              modelDependenciesProvider)
//
//      Map handlerMethods = [(requestMappingInfo("somePath/")): dummyHandlerMethod()]
//      def requestHandlerMapping = Mock(RequestMappingHandlerMapping)
//      requestHandlerMapping.getHandlerMethods() >> handlerMethods
//
//      ApiListingReferenceScanner scanner = new ApiListingReferenceScanner()
//      scanner.setRequestMappingHandlerMapping([requestHandlerMapping])
//      scanner.setResourceGroupingStrategy(new ClassOrApiAnnotationResourceGrouping())
//      scanner.setSwaggerGroup("swaggerGroup")
//
//      scanner.setSwaggerPathProvider(swaggerPathProvider)
//      swaggerApiResourceListing.setModelProvider(modelProvider)
//      swaggerApiResourceListing.setApiListingReferenceScanner(scanner)
//
//    when:
//      swaggerApiResourceListing.initialize()
//      ResourceListing resourceListing = swaggerCache.getSwagger("swaggerGroup")
//
//    then:
//      ApiListingReference apiListingReference = resourceListing.apis().head()
//      apiListingReference.path() == "http://localhost:8080/context-path/api-docs/swaggerGroup/dummy-class"
//      apiListingReference.position() == 0
//      fromOption(apiListingReference.description()) == "Dummy Class"
//
//    and:
//      ApiListing apiListing =
//            swaggerCache.swaggerApiListingMap['swaggerGroup']['dummy-class']
//      apiListing.swaggerVersion() == '1.2'
//      apiListing.basePath() == 'http://localhost:8080/context-path'
//      apiListing.resourcePath() == '/somePath'
//  }
//
//  def "Should sort based on position"() {
//    given:
//      SwaggerCache swaggerCache = new SwaggerCache();
//      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
//      swaggerApiResourceListing.setApiListingReferenceOrdering(ordering)
//
//      ApiListingReferenceScanner apiListingReferenceScanner = Mock()
//      apiListingReferenceScanner.getApiListingReferences() >> [
//            new ApiListingReference("/b", toOption('b'), 1),
//            new ApiListingReference("/a", toOption('a'), 2)
//      ]
//
//      swaggerApiResourceListing.apiListingReferenceScanner = apiListingReferenceScanner
//
//    when:
//      swaggerApiResourceListing.initialize()
//      def apis = fromScalaList(swaggerCache.getSwagger('default').apis())
//    then:
//      apis[0].position == firstPosition
//      apis[0].path == firstPath
//
//    where:
//      ordering                                     | firstPath | firstPosition
//      new ResourceListingPositionalOrdering()      | '/b'      | 1
//      new ResourceListingLexicographicalOrdering() | '/a'      | 2
//  }
}
