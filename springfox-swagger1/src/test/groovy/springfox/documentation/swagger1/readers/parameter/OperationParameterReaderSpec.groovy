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

package springfox.documentation.swagger1.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.service.Parameter
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.models.Example
import springfox.documentation.spring.web.dummy.models.Treeish
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.spring.web.readers.operation.OperationParameterReader
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static springfox.documentation.schema.AlternateTypeRules.*

class OperationParameterReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        SwaggerPluginsSupport,
        ModelProviderForServiceSupport {
  OperationParameterReader sut
  def pluginsManager

  def setup() {
    def typeResolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    pluginsManager = swaggerServicePlugins([
        new SwaggerDefaultConfiguration(new Defaults(), typeResolver, new DefaultPathProvider())])
    plugin
        .ignoredParameterTypes(
            ServletRequest,
            ServletResponse,
            HttpServletRequest,
            HttpServletResponse,
            BindingResult, ServletContext,
            DummyModels.Ignorable.class)
        .alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
        .configure(contextBuilder)

    def expander = new ModelAttributeParameterExpander(
        new FieldProvider(typeResolver),
        new AccessorsProvider(typeResolver),
        enumTypeDeterminer)

    expander.pluginsManager = pluginsManager

    sut = new OperationParameterReader(expander, enumTypeDeterminer)
    sut.pluginsManager = pluginsManager
  }

  def "Should ignore ignorables"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize

    where:
    handlerMethod                                                        | expectedSize
    dummyHandlerMethod('methodWithServletRequest', ServletRequest.class) | 0
    dummyHandlerMethod('methodWithBindingResult', BindingResult.class)   | 0
    dummyHandlerMethod('methodWithInteger', Integer.class)               | 1
    dummyHandlerMethod('methodWithAnnotatedInteger', Integer.class)      | 0
  }

  def "Should read a request mapping method without APIParameter annotation"() {
    given:
    HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSinglePathVariable', String.class)

    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)


    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    Parameter parameter = operation.parameters[0]
    assert parameter."$property" == expectedValue

    where:
    property        | expectedValue
    'name'          | 'businessId'
    'required'      | false
    'allowMultiple' | false
    'paramType'     | null
    //TODO: add more properties and readers
  }

  def "Should expand ModelAttribute request params"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithModelAttribute', Example.class))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == 10

    Parameter annotatedFooParam = operation.parameters.find { it.name == "foo" }
    annotatedFooParam != null
    annotatedFooParam.getDescription() == 'description of foo'
    annotatedFooParam.required
    annotatedFooParam.allowableValues != null

    Parameter annotatedBarParam = operation.parameters.find { it.name == "bar" }
    annotatedBarParam.getDescription() == 'description of bar'
    !annotatedBarParam.required
    annotatedBarParam.allowableValues == null

    Parameter unannotatedEnumTypeParam = operation.parameters.find { it.name == "enumType" }
    unannotatedEnumTypeParam.getDescription() == null
    unannotatedEnumTypeParam.allowableValues != null

    Parameter annotatedEnumTypeParam = operation.parameters.find { it.name == "annotatedEnumType" }
    annotatedEnumTypeParam.getDescription() == 'description of annotatedEnumType'
    annotatedEnumTypeParam.allowableValues != null

    Parameter unannotatedNestedTypeNameParam = operation.parameters.find { it.name == "nestedType.name" }
    unannotatedNestedTypeNameParam != null
    unannotatedNestedTypeNameParam.getDescription() == null

    Parameter annotatedAllCapsSetParam = operation.parameters.find { it.name == "allCapsSet" }
    annotatedAllCapsSetParam.getDescription() == 'description of allCapsSet'
    !annotatedAllCapsSetParam.required
    annotatedAllCapsSetParam.allowableValues == null

    Parameter unannotatedParentBeanParam = operation.parameters.find { it.name == "parentBeanProperty" }
    unannotatedParentBeanParam.getDescription() == null

    Parameter localDateTime = operation.parameters.find { it.name == "localDateTime" }
    localDateTime.required
    localDateTime.getDescription() == 'local date time desc dd-MM-yyyy hh:mm:ss'
  }

  def "Should expand ModelAttribute request param if param has treeish field"() {
    given:
    def methodResolver = new HandlerMethodResolver(new TypeResolver())

    OperationContext operationContext = new OperationContext(
        new OperationBuilder(new CachingOperationNameGenerator()),
        RequestMethod.GET,
        new RequestMappingContext("0",
            documentationContext(),
            new WebMvcRequestHandler(
                Paths.ROOT,
                methodResolver,
                requestMappingInfo("/somePath"),
                dummyHandlerMethod('methodWithTreeishModelAttribute', Treeish.class))),
        0)
    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == 1
    Parameter annotatedBarParam = operation.parameters[0]
    annotatedBarParam != null
    annotatedBarParam.name == 'treeishField'
  }

  def "Should not expand unannotated request params"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize

    where:
    handlerMethod                                                    | expectedSize
    dummyHandlerMethod('methodWithoutModelAttribute', Example.class) | 10
  }
}
