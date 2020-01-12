/*
 *
 *  Copyright 2016-2019 the original author or authors.
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
package springfox.documentation.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

class SwaggerOperationModelsProviderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "should read from annotations"() {
    given:
    def methodResolver = new HandlerMethodResolver(new TypeResolver())
    RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
        [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
    )
    RequestMappingContext requestContext = new RequestMappingContext(
        "0",
        documentationContext(),
        new WebMvcRequestHandler(
            Paths.ROOT,
            methodResolver,
            requestMappingInfo,
            dummyHandlerMethod(operationName)))
    SwaggerOperationModelsProvider sut = new SwaggerOperationModelsProvider(new TypeResolver())
    when:
    sut.apply(requestContext)
    def models = requestContext.operationModelsBuilder().build()

    then:
    models.size() == modelCount
    and:
    !sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
    where:
    operationName                    | modelCount
    'dummyMethod'                    | 1
    'methodWithPosition'             | 1
    'methodApiResponseClass'         | 2
    'methodAnnotatedWithApiResponse' | 2
  }

}
