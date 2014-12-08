package com.mangofactory.swagger.configuration
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.core.RequestMappingEvaluator
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.ModelDependencyProvider
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.models.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.swagger.models.dto.ApiInfo
import com.mangofactory.swagger.models.dto.ApiKey
import com.mangofactory.swagger.models.dto.ApiListing
import com.mangofactory.swagger.models.dto.ApiListingReference
import com.mangofactory.swagger.models.dto.AuthorizationType
import com.mangofactory.swagger.models.dto.ResourceListing
import com.mangofactory.swagger.models.property.bean.AccessorsProvider
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldProvider
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering
import com.mangofactory.swagger.ordering.ResourceListingPositionalOrdering
import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.paths.SwaggerPathProvider
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher
import com.wordnik.swagger.model.*
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([RequestMappingSupport, SpringSwaggerConfigSupport])
class SwaggerApiResourceListingSpec extends Specification {

  def "assessors"() {
    given:
      SwaggerCache cache = new SwaggerCache()
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(cache, null)
      List<AuthorizationType> authTypes = Arrays.asList(new ApiKey("", ""))
      swaggerApiResourceListing.setAuthorizationTypes(authTypes)
      AbsoluteSwaggerPathProvider provider = new AbsoluteSwaggerPathProvider()
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
      def apiListingReferenceList = resourceListing.getApis()
      def authorizationTypes = resourceListing.getAuthorizations()

      resourceListing.getApiVersion() == "1"
      resourceListing.getSwaggerVersion() == "1.2"

      resourceListing.getInfo() == null
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
      swaggerApiResourceListing.apiInfo.getTitle() == "title"
      swaggerApiResourceListing.apiInfo.getDescription() == "description"
      swaggerApiResourceListing.apiInfo.getTermsOfServiceUrl() == "terms"
      swaggerApiResourceListing.apiInfo.getContact() == "contact"
      swaggerApiResourceListing.apiInfo.getLicense() == "license"
      swaggerApiResourceListing.apiInfo.getLicenseUrl() == "licenseUrl"
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
      def authorizationTypes = resourceListing.getAuthorizations()
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

      SwaggerPathProvider swaggerPathProvider = new AbsoluteSwaggerPathProvider(servletContext: servletContext())
      swaggerApiResourceListing.setSwaggerPathProvider(swaggerPathProvider)

      def settings = new SwaggerGlobalSettings()
      settings.setIgnorableParameterTypes(new SpringSwaggerConfig().defaultIgnorableParameterTypes())
      SpringSwaggerConfig springSwaggerConfig = springSwaggerConfig()
      settings.alternateTypeProvider = springSwaggerConfig.defaultAlternateTypeProvider();
      swaggerApiResourceListing.setSwaggerGlobalSettings(settings)

      def resolver = new TypeResolver()
      def objectMapper = new ObjectMapper()
      def fields = new FieldProvider(resolver)
      def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

      def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(resolver), resolver,
              settings.alternateTypeProvider, namingStrategy)
      def fieldModelPropertyProvider =
              new FieldModelPropertyProvider(fields, settings.alternateTypeProvider, namingStrategy)
      def constructorModelPropertyProvider = new ConstructorModelPropertyProvider(fields,
              settings.alternateTypeProvider, namingStrategy)

      def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
              fieldModelPropertyProvider, constructorModelPropertyProvider)
      modelPropertiesProvider.objectMapper = objectMapper
      def modelDependenciesProvider = new ModelDependencyProvider(resolver, settings.alternateTypeProvider,
              modelPropertiesProvider)
      ModelProvider modelProvider = new DefaultModelProvider(resolver, settings.alternateTypeProvider,
              modelPropertiesProvider,
              modelDependenciesProvider)

      Map handlerMethods = [(requestMappingInfo("somePath/")): dummyHandlerMethod()]
      def requestHandlerMapping = Mock(RequestMappingHandlerMapping)
      requestHandlerMapping.getHandlerMethods() >> handlerMethods

      def requestMappingEvaluator = new RequestMappingEvaluator(newArrayList(), new RegexRequestMappingPatternMatcher(),
              newArrayList(".*"))
      ApiListingReferenceScanner scanner = new ApiListingReferenceScanner()
      scanner.setRequestMappingHandlerMapping([requestHandlerMapping])
      scanner.setResourceGroupingStrategy(new ClassOrApiAnnotationResourceGrouping())
      scanner.setSwaggerGroup("swaggerGroup")
      scanner.setRequestMappingEvaluator(requestMappingEvaluator)

      scanner.setSwaggerPathProvider(swaggerPathProvider)
      swaggerApiResourceListing.setModelProvider(modelProvider)
      swaggerApiResourceListing.setApiListingReferenceScanner(scanner)

      swaggerApiResourceListing.setRequestMappingEvaluator(requestMappingEvaluator)

    when:
      swaggerApiResourceListing.initialize()
      ResourceListing resourceListing = swaggerCache.getResourceListing("swaggerGroup")

    then:
      ApiListingReference apiListingReference = resourceListing.getApis().head()
      apiListingReference.getPath() == "http://localhost:8080/context-path/api-docs/swaggerGroup/dummy-class"
      apiListingReference.getPosition() == 0
      apiListingReference.getDescription() == "Dummy Class"

    and:
      ApiListing apiListing =
            swaggerCache.swaggerApiListingMap['swaggerGroup']['dummy-class']
      apiListing.getSwaggerVersion() == '1.2'
      apiListing.getBasePath() == 'http://localhost:8080/context-path'
      apiListing.getResourcePath() == '/somePath'
  }

  def "Should sort based on position"() {
    given:
      SwaggerCache swaggerCache = new SwaggerCache();
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(swaggerCache, "default")
      swaggerApiResourceListing.setApiListingReferenceOrdering(ordering)

      ApiListingReferenceScanner apiListingReferenceScanner = Mock()
      apiListingReferenceScanner.getApiListingReferences() >> [
            new ApiListingReference("/b", 'b', 1),
            new ApiListingReference("/a", 'a', 2)
      ]

      swaggerApiResourceListing.apiListingReferenceScanner = apiListingReferenceScanner

    when:
      swaggerApiResourceListing.initialize()
      def apis = swaggerCache.getResourceListing('default').getApis()
    then:
      apis[0].position == firstPosition
      apis[0].path == firstPath

    where:
      ordering                                     | firstPath | firstPosition
      new ResourceListingPositionalOrdering()      | '/b'      | 1
      new ResourceListingLexicographicalOrdering() | '/a'      | 2
  }
}
