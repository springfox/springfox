/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Unroll
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.ApiListing
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spi.service.ApiListingScannerPlugin
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.Bug2219ListingScanner
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyControllerWithResourcePath
import springfox.documentation.spring.web.mixins.ApiDescriptionSupport
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import static java.util.Collections.*
import static org.springframework.http.MediaType.*
import static springfox.documentation.builders.PathSelectors.*
import static springfox.documentation.spring.web.scanners.ApiListingScanner.*

class ApiListingScannerSpec
    extends DocumentationContextSpec
    implements AuthSupport,
        RequestMappingSupport,
        ModelProviderForServiceSupport,
        ApiDescriptionSupport {
  
  ApiDescriptionReader apiDescriptionReader
  ApiModelReader apiModelReader
  ApiListingScanningContext listingContext
  ApiListingScanner scanner
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def setup() {
    SecurityContext securityContext = SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(regex('/anyPath.*'))
        .build()

    contextBuilder.withResourceGroupingStrategy(new SpringGroupingStrategy())
    plugin
        .securityContexts(singletonList(securityContext))
        .configure(contextBuilder)
    apiDescriptionReader = Mock(ApiDescriptionReader)
    apiDescriptionReader.read(_) >> []
    apiModelReader = Mock(ApiModelReader)
    apiModelReader.read(_) >> new HashMap<>()
    scanner = new ApiListingScanner(apiDescriptionReader, apiModelReader, defaultWebPlugins())
  }

  def "Should create an api listing for a single resource grouping "() {
    given:
    def context = documentationContext()
    RequestMappingContext requestMappingContext = requestMapping(context)
    listingContext = listingContext(requestMappingContext, context)

    when:
    apiDescriptionReader.read(requestMappingContext) >> []

    and:
    def scanned = scanner.scan(listingContext)

    then:
    scanned.containsKey("businesses")
    Collection<ApiListing> listings = scanned.get("businesses")
    listings.first().consumes == [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE] as Set
    listings.first().produces == [APPLICATION_JSON_VALUE] as Set
    listings.first().description == 'Dummy Class'
  }

  def "should assign global authorizations"() {
    given:
    def context = documentationContext()
    def requestMappingContext = requestMapping(context)
    listingContext = listingContext(requestMappingContext, context)

    when:
    Map<String, List<ApiListing>> apiListingMap = scanner.scan(listingContext)

    then:
    Collection<ApiListing> listings = apiListingMap.get('businesses')
    listings.first().getSecurityReferences().size() == 0
  }

  def "Should create an api listing for an api description with no backing controller"() {
    given:
    plugin.groupName("different-group")
    def context = documentationContext()
    Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = new HashMap<>()
    listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)

    and:
    def sut = new ApiListingScanner(
        apiDescriptionReader,
        apiModelReader,
        customWebPlugins(
            [],
            [],
            [],
            [],
            [],
            [],
            [additionalListingsScanner()]
        ))

    when:
    def scanned = sut.scan(listingContext)

    then:
    scanned.containsKey("different-group")
    Collection<ApiListing> listings = scanned.get("different-group")
    listings.size() == 1
    listings.first().apis.first().description == 'This is a bug-fix for 2219'
  }


  def "Should not mix existing apis with apis with no backing controller"() {
    given:
    plugin.groupName("different-group")
    def context = documentationContext()
    def sut = new ApiListingScanner(
        apiDescriptionReader,
        apiModelReader,
        customWebPlugins(
            [],
            [],
            [],
            [],
            [],
            [],
            [additionalListingsScanner()]
        ))

    and:
    RequestMappingContext requestMappingContext = requestMapping(context)
    listingContext = listingContext(requestMappingContext, context)

    when:
    def scanned = sut.scan(listingContext)

    then:
    scanned.size() == 2

    and:
    scanned.containsKey("different-group")
    def differentListings = scanned.get("different-group")
    differentListings.first().apis.first().description == 'This is a bug-fix for 2219'

    and:
    scanned.containsKey("businesses")
    def businessListings = scanned.get("businesses")
    businessListings.size() == 1
    businessListings.first().description == "Dummy Class"
  }

  def "should assign resource form @RequestMapping annotation"() {
    given:
    def context = documentationContext()
    def requestMappingContext = requestMapping(context, "dummyMethod")
    def resourceGroupRequestMappings = new HashMap<>()
    resourceGroupRequestMappings.put(
        new ResourceGroup("resourcePath", DummyControllerWithResourcePath),
        [requestMappingContext])

    listingContext = new ApiListingScanningContext(context, resourceGroupRequestMappings)

    when:
    apiDescriptionReader.read(requestMappingContext) >> []

    and:
    def scanned = scanner.scan(listingContext)

    then:
    scanned.containsKey("resourcePath")
    Collection<ApiListing> listings = scanned.get("resourcePath")
    listings.first().resourcePath == "/resource-path"
  }

  @Unroll
  def "should find longest common path"() {
    given:
    String result = longestCommonPath(apiDescriptions(paths)).orElse(null)

    expect:
    result == expected

    where:
    paths                                        | expected
    []                                           | null
    ['/a/b', '/a/b']                             | '/a/b'
    ['/a/b', '/a/b/c']                           | '/a/b'
    ['/a/b', '/a/']                              | '/a'
    ['/a/b', '/a/d/e/f']                         | '/a'
    ['/a/b/c/d/e/f', '/a', '/a/b']               | '/a'
    ['/d', '/e', 'f']                            | '/'
    ['/a/b/c', '/a/b/c/d/e/f', '/a/b/c/d/e/f/g'] | '/a/b/c'
  }

  def listingContext(
      RequestMappingContext requestMappingContext,
      DocumentationContext context) {

    ResourceGroup resourceGroup = new ResourceGroup("businesses", DummyClass)
    Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings = new HashMap<>()
    resourceGroupRequestMappings.put(resourceGroup, [requestMappingContext])
    new ApiListingScanningContext(context, resourceGroupRequestMappings)
  }

  def requestMapping(
      DocumentationContext context,
      String methodName = "methodWithConcreteResponseBody",
      String path = "/businesses") {

    RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
    RequestMappingContext requestMappingContext =
        new RequestMappingContext(
            "0",
            context,
            new WebMvcRequestHandler(
                Paths.ROOT,
                methodResolver,
                requestMappingInfo,
                dummyHandlerMethod(methodName)))
    requestMappingContext
  }

  ApiListingScannerPlugin additionalListingsScanner() {
    new Bug2219ListingScanner()
  }
}