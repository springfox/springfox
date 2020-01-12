/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition
import spock.lang.Unroll
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.service.Parameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.dummy.AlternateTypeContainer
import springfox.documentation.spring.web.dummy.DummyModels
import springfox.documentation.spring.web.dummy.ToReplaceWithString
import springfox.documentation.spring.web.dummy.models.Example
import springfox.documentation.spring.web.dummy.models.Treeish
import springfox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.OperationParameterReader

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static springfox.documentation.schema.AlternateTypeRules.*

class OperationParameterReaderSpec
    extends DocumentationContextSpec
    implements RequestMappingSupport,
        ModelProviderForServiceSupport  {
  OperationParameterReader sut
  def pluginsManager = defaultWebPlugins()

  def setup() {
    def typeResolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    plugin
        .ignoredParameterTypes(ServletRequest, ServletResponse, HttpServletRequest,
        HttpServletResponse, BindingResult, ServletContext,
        DummyModels.Ignorable.class
    )
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

  @Unroll
  def "Should ignore ignorables"() {
    given:
    OperationContext operationContext = operationContext(
        documentationContext(),
        handlerMethod,
        0,
        requestMappingInfo("/somePath"))

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

  def "Should consider alternate types"() {
    given:
    contextBuilder.rules([newRule(ToReplaceWithString, String)])
    OperationContext operationContext = operationContext(
        documentationContext(),
        handlerMethod,
        0,
        requestMappingInfo("/somePath"))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize
    operation.parameters[0].name == "stringValue"
    operation.parameters[0].modelRef.type == "string"

    where:
    handlerMethod                                                               | expectedSize
    dummyHandlerMethod('methodWithAlternateType', AlternateTypeContainer.class) | 1
  }

  def matchesEmptyExample(List<Parameter> parameters) {
    assert parameters.size() == 10

    Parameter annotatedFooParam = parameters.find { it.name == "foo" }
    assert annotatedFooParam != null
    assert annotatedFooParam.getDescription() == null
    assert !annotatedFooParam.required
    assert annotatedFooParam.allowableValues == null

    Parameter annotatedBarParam = parameters.find { it.name == "bar" }
    assert annotatedBarParam.getDescription() == null
    assert !annotatedBarParam.required
    assert annotatedBarParam.allowableValues == null

    Parameter unannotatedEnumTypeParam = parameters.find { it.name == "enumType" }
    assert unannotatedEnumTypeParam.getDescription() == null
    assert unannotatedEnumTypeParam.allowableValues != null

    Parameter annotatedEnumTypeParam = parameters.find { it.name == "annotatedEnumType" }
    assert annotatedEnumTypeParam.getDescription() == null
    assert annotatedEnumTypeParam.allowableValues != null

    Parameter unannotatedNestedTypeNameParam = parameters.find { it.name == "nestedType.name" }
    assert unannotatedNestedTypeNameParam != null
    assert unannotatedNestedTypeNameParam.getDescription() == null

    Parameter annotatedAllCapsSetParam = parameters.find { it.name == "allCapsSet" }
    assert annotatedAllCapsSetParam.getDescription() == null
    assert !annotatedAllCapsSetParam.required
    assert annotatedAllCapsSetParam.allowableValues == null

    Parameter unannotatedParentBeanParam = parameters.find { it.name == "parentBeanProperty" }
    assert unannotatedParentBeanParam.getDescription() == null

    Parameter localDateTime = parameters.find { it.name == "localDateTime" }
    assert !localDateTime.required
    assert localDateTime.getDescription() == null
    true
  }

  def "Should expand ModelAttribute request params as type query"() {
    given:
    plugin.directModelSubstitute(LocalDateTime, String)
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            dummyHandlerMethod('methodWithModelAttribute', Example.class),
            0,
            requestMappingInfo("/somePath"))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    matchesEmptyExample(operation.parameters)

    operation.parameters.every {
      it.paramType == "query"
    }
  }

  def "Should expand multipart ModelAttribute request params as formData"() {
    given:
    plugin.directModelSubstitute(LocalDateTime, String)
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            dummyHandlerMethod('methodWithModelAttribute', Example.class),
            0,
            requestMappingInfo(
                "/somePath",
                ["consumesRequestCondition": new ConsumesRequestCondition("multipart/form-data")]),
            RequestMethod.POST)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    matchesEmptyExample(operation.parameters)

    operation.parameters.every {
      it.paramType == "formData"
    }
  }

  def "Should expand ModelAttribute request param if param has treeish field"() {
    given:
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            dummyHandlerMethod('methodWithTreeishModelAttribute', Treeish.class),
            0,
            requestMappingInfo("/somePath"))

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
        operationContext(
            documentationContext(),
            handlerMethod,
            0,
            requestMappingInfo("/somePath"))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize

    where:
    handlerMethod                                                    | expectedSize
    dummyHandlerMethod('methodWithoutModelAttribute', Example.class) | 10
  }

  def "Should not expand @RequestParam or @PathVariable annotated params"() {
    given:
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            handlerMethod,
            0,
            requestMappingInfo("/somePath"))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize

    where:
    handlerMethod                                                | expectedSize
    dummyHandlerMethod('methodWithURIAsRequestParam', URI.class) | 1
    dummyHandlerMethod('methodWithURIAsPathVariable', URI.class) | 1
  }

  def "OperationParameterReader supports all documentationTypes"() {
    given:
    def sut = new OperationParameterReader(Mock(ModelAttributeParameterExpander), new JacksonEnumTypeDeterminer())
    sut.pluginsManager = defaultWebPlugins()

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
  }
}
