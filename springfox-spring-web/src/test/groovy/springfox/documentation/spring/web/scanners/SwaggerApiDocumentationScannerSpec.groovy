/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.scanners

import springfox.documentation.spring.web.AbstractPathProvider
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.Documentation
import springfox.documentation.service.ResourceListing
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.RelativePathProvider
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import static com.google.common.collect.Maps.*
import static springfox.documentation.builders.PathSelectors.regex

@Mixin([RequestMappingSupport])
class SwaggerApiDocumentationScannerSpec extends DocumentationContextSpec {

  ApiListingReferenceScanner listingReferenceScanner = Mock(ApiListingReferenceScanner)
  ApiListingScanner listingScanner = Mock(ApiListingScanner)
  ApiDocumentationScanner  swaggerApiResourceListing = new ApiDocumentationScanner(listingReferenceScanner, listingScanner)

  def "default swagger resource"() {
    when: "I create a swagger resource"
      listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Documentation scanned = swaggerApiResourceListing.scan(context())

    then: "I should should have the correct defaults"
      ResourceListing resourceListing = scanned.resourceListing
      def apiListingReferenceList = resourceListing.getApis()
      def authorizationTypes = resourceListing.getSecuritySchemes()

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
      plugin
              .groupName("groupName")
              .select()
                .paths(regex(".*"))
                .build()
              .apiInfo(expected)
              .configure(contextBuilder)
      listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Documentation scanned = swaggerApiResourceListing.scan(context())
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
      ApiKey apiKey = new ApiKey("mykey", "api_key", "header",)
    when:
      plugin
              .groupName("groupName")
              .select()
                .paths(regex(".*"))
                .build()
              .securitySchemes([apiKey])
              .configure(contextBuilder)
      listingReferenceScanner.scan(_) >> new ApiListingReferenceScanResult([], newHashMap())
    and:
      Documentation scanned = swaggerApiResourceListing.scan(context())
    then:
      ResourceListing resourceListing = scanned.resourceListing
      def authorizationTypes = resourceListing.getSecuritySchemes()
      def apiKeyAuthType = authorizationTypes[0]
      apiKeyAuthType instanceof ApiKey
      apiKeyAuthType.name == "mykey"
      apiKeyAuthType.keyname == "api_key"
      apiKeyAuthType.passAs == "header"
  }

  def "resource with mocked apis"() {
    given:
      AbstractPathProvider pathProvider = new RelativePathProvider(servletContext())
      plugin
              .groupName("groupName")
              .select()
                .paths(regex(".*"))
                .build()
              .pathProvider(pathProvider)
              .configure(contextBuilder)

      RequestMappingContext requestMappingContext = new RequestMappingContext(context(),
          requestMappingInfo("somePath/"), dummyHandlerMethod())
    and:
      def mockListingRef = Mock(ApiListingReference)
      mockListingRef.path >> "/some/path"
    when:
      listingReferenceScanner.scan(_) >>
              new ApiListingReferenceScanResult([mockListingRef], [resourceGroup: [requestMappingContext]])
    and:
      Documentation scanned = swaggerApiResourceListing.scan(context())
      scanned.resourceListing != null

    then:
      scanned.groupName == "groupName"
      1 * listingReferenceScanner.scan(_) >>
              new ApiListingReferenceScanResult([mockListingRef], [resourceGroup: [requestMappingContext]])
  }

  def "Should sort based on position"() {
    given:
      def ordering = new Defaults().apiListingReferenceOrdering()
      plugin
              .groupName("groupName")
              .select()
                .paths(regex(".*"))
                .build()
              .apiListingReferenceOrdering(ordering)
              .configure(contextBuilder)

      def refs = [
            new ApiListingReference("/b", 'b', 1),
            new ApiListingReference("/c", 'c', 2),
            new ApiListingReference("/a", 'a', 2)
      ]
      listingReferenceScanner.scan(_) >>
              new ApiListingReferenceScanResult(refs, newHashMap())

    when:
      Documentation scanned = swaggerApiResourceListing.scan(context())
      def apis = scanned.resourceListing.getApis()
    then:
      apis[index].position == position
      apis[index].path == path

    where:
      index | path  | position
      0     | '/b' | 1
      1     | '/a' | 2
      2     | '/c' | 2
  }
}
