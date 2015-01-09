package com.mangofactory.swagger.configuration
import com.mangofactory.service.model.ApiInfo
import com.mangofactory.service.model.ApiKey
import com.mangofactory.service.model.ApiListingReference
import com.mangofactory.service.model.Group
import com.mangofactory.service.model.ResourceListing
import com.mangofactory.spring.web.plugins.DocumentationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.spring.web.scanners.ApiGroupScanner
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.spring.web.ordering.ResourceListingLexicographicalOrdering
import com.mangofactory.spring.web.ordering.ResourceListingPositionalOrdering
import com.mangofactory.swagger.web.AbsolutePathProvider
import com.mangofactory.spring.web.PathProvider
import com.mangofactory.spring.web.scanners.ApiListingReferenceScanResult
import com.mangofactory.spring.web.scanners.ApiListingReferenceScanner
import com.mangofactory.spring.web.scanners.ApiListingScanner
import com.mangofactory.spring.web.scanners.RequestMappingContext

import static com.google.common.collect.Maps.*

@Mixin([RequestMappingSupport])
class SwaggerApiResourceListingSpec extends DocumentationContextSpec {
  ApiListingReferenceScanner listingReferenceScanner = Mock(ApiListingReferenceScanner)
  ApiListingScanner listingScanner = Mock(ApiListingScanner)
  ApiGroupScanner  swaggerApiResourceListing = new ApiGroupScanner(listingReferenceScanner, listingScanner)

  def "default swagger resource"() {
    when: "I create a swagger resource"
      listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Group scanned = swaggerApiResourceListing.scan(context())

    then: "I should should have the correct defaults"
      ResourceListing resourceListing = scanned.resourceListing
      def apiListingReferenceList = resourceListing.getApis()
      def authorizationTypes = resourceListing.getAuthorizations()

      scanned.groupName == "default"
      resourceListing.getApiVersion() == "1.0"

      resourceListing.getInfo() != null
      apiListingReferenceList == []
      authorizationTypes == []
  }

  def "resource with api info"() {
    given:
      ApiInfo expected = new ApiInfo("title", "description", "1.0", "terms", "contact", "license", "licenseUrl")
    when:
      DocumentationContext context = plugin
              .groupName("groupName")
              .includePatterns(".*")
              .apiInfo(expected)
              .build(contextBuilder)
      listingReferenceScanner.scan(context) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Group scanned = swaggerApiResourceListing.scan(context)
    then:
      ApiInfo actual = scanned.getResourceListing().getInfo()
      actual.getTitle() == expected.getTitle()
      actual.getVersion() == expected.getVersion()
      actual.getDescription() == expected.getDescription()
      actual.getTermsOfServiceUrl() == expected.getTermsOfServiceUrl()
      actual.getContact() == expected.getContact()
      actual.getLicense() == expected.getLicense()
      actual.getLicenseUrl() == expected.getLicenseUrl()
  }

  def "resource with authorization types"() {
    given:
      ApiKey apiKey = new ApiKey("api_key", "header")
    when:
      DocumentationContext context = plugin
              .groupName("groupName")
              .includePatterns(".*")
              .authorizationTypes([apiKey])
              .build(contextBuilder)
      listingReferenceScanner.scan(context) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Group scanned = swaggerApiResourceListing.scan(context)
    then:
      ResourceListing resourceListing = scanned.resourceListing
      def authorizationTypes = resourceListing.getAuthorizations()
      def apiKeyAuthType = authorizationTypes[0]
      apiKeyAuthType instanceof ApiKey
      apiKeyAuthType.keyname == "api_key"
      apiKeyAuthType.passAs == "header"
  }

  def "resource with mocked apis"() {
    given:
      PathProvider pathProvider = new AbsolutePathProvider(servletContext: servletContext())
      DocumentationContext context = plugin
              .groupName("groupName")
              .includePatterns(".*")
              .pathProvider(pathProvider)
              .build(contextBuilder)

      RequestMappingContext requestMappingContext = new RequestMappingContext(context, (requestMappingInfo
              ("somePath/")), dummyHandlerMethod())

    when:
      listingReferenceScanner.scan(context) >>
              new ApiListingReferenceScanResult([Mock(ApiListingReference)], [resourceGroup: [requestMappingContext]])
    and:
      Group scanned = swaggerApiResourceListing.scan(context)
      scanned.resourceListing != null

    then:
      scanned.groupName == "groupName"
      1 * listingReferenceScanner.scan(context) >>
              new ApiListingReferenceScanResult([Mock(ApiListingReference)], [resourceGroup: [requestMappingContext]])
  }

  def "Should sort based on position"() {
    given:
      DocumentationContext context = plugin
              .groupName("groupName")
              .includePatterns(".*")
              .apiListingReferenceOrdering(ordering)
              .build(contextBuilder)

      def refs = [
            new ApiListingReference("/b", 'b', 1),
            new ApiListingReference("/a", 'a', 2)
      ]
      listingReferenceScanner.scan(context) >>
              new ApiListingReferenceScanResult(refs, newHashMap())

    when:
      Group scanned = swaggerApiResourceListing.scan(context)
      def apis = scanned.resourceListing.getApis()
    then:
      apis[0].position == firstPosition
      apis[0].path == firstPath

    where:
      ordering                                     | firstPath | firstPosition
      new ResourceListingPositionalOrdering()      | '/b'      | 1
      new ResourceListingLexicographicalOrdering() | '/a'      | 2
  }
}
