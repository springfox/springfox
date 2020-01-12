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
import springfox.documentation.service.ApiDescription
import springfox.documentation.service.Operation
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.ApiOperationReader
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

class ApiDescriptionReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {

  def "should generate an api description for each request mapping pattern"() {
    given:
    def operationReader = Mock(ApiOperationReader)
    ApiDescriptionReader sut =
        new ApiDescriptionReader(operationReader, defaultWebPlugins(), new ApiDescriptionLookup())

    and:
    plugin.pathProvider(pathProvider)
    RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
        [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
    )
    RequestMappingContext mappingContext = new RequestMappingContext(
        "0",
        documentationContext(),
        new WebMvcRequestHandler(
            Paths.ROOT,
            new HandlerMethodResolver(new TypeResolver()),
            requestMappingInfo,
            dummyHandlerMethod()))
    operationReader.read(_) >> [Mock(Operation), Mock(Operation)]

    when:
    def descriptionList = sut.read(mappingContext)

    then:
    descriptionList.size() == 2

    ApiDescription apiDescription = descriptionList[0]
    ApiDescription secondApiDescription = descriptionList[1]

    apiDescription.getPath() == prefix + '/somePath/{businessId}'
    apiDescription.getDescription() == dummyHandlerMethod().method.name
    !apiDescription.isHidden()

    secondApiDescription.getPath() == prefix + '/somePath/{businessId}'
    secondApiDescription.getDescription() == dummyHandlerMethod().method.name
    !secondApiDescription.isHidden()

    where:
    pathProvider              | prefix
    new DefaultPathProvider() | ""
  }

  def "should handle exceptions gracefully"() {
    given:
    def operationReader = Mock(ApiOperationReader)
    ApiDescriptionReader sut =
        new ApiDescriptionReader(
            operationReader,
            defaultWebPlugins(),
            new ApiDescriptionLookup())

    and:
    RequestMappingInfo requestMappingInfo = requestMappingInfo(
        "/doesNotMatterForThisTest",
        [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}')])
    RequestMappingContext mappingContext = new RequestMappingContext(
        "0",
        documentationContext(),
        new WebMvcRequestHandler(
            Paths.ROOT,
            new HandlerMethodResolver(new TypeResolver()),
            requestMappingInfo,
            dummyHandlerMethod()))
    operationReader.read(_) >> { throw new StackOverflowError("ouch") }

    when:
    def descriptionList = sut.read(mappingContext)

    then:
    descriptionList.size() == 0
  }

  @Unroll("Sanitization works for #mappingPattern")
  def "should sanitize request mapping endpoints"() {
    expect:
    Paths.sanitizeRequestMappingPattern(mappingPattern) == expected

    where:
    mappingPattern                                  | expected
    ""                                              | "/"
    "/"                                             | "/"
    "/businesses"                                   | "/businesses"
    "/{businessId:\\w+}"                            | "/{businessId}"
    "/{businessId:\\d{3}}"                          | "/{businessId}"
    "/{businessId:\\d{3}}/{productId:\\D{3}\\d{3}}" | "/{businessId}/{productId}"
    "/businesses/{businessId}"                      | "/businesses/{businessId}"
    "/businesses/{businessId}/add"                  | "/businesses/{businessId}/add"
    "/foo/bar:{baz}"                                | "/foo/bar:{baz}"
    "/foo:{foo}/bar:{baz}"                          | "/foo:{foo}/bar:{baz}"
    "/foo/bar:{baz:\\w+}"                           | "/foo/bar:{baz}"
    "/{businessId:\\d{3}}:{productId:\\D{3}\\d{3}}" | "/{businessId}:{productId}"
    "/{businessId:\\d{3}}:{productId:\\D{3}\\d{3}[(abcd){9,3}?(?!abc)?]+}" | "/{businessId}:{productId}"
  }
}
