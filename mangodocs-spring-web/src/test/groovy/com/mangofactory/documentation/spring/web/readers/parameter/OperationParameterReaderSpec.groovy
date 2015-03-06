package com.mangofactory.documentation.spring.web.readers.parameter
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.service.Parameter
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.dummy.DummyModels
import com.mangofactory.documentation.spring.web.dummy.models.Example
import com.mangofactory.documentation.spring.web.dummy.models.Treeish
import com.mangofactory.documentation.spring.web.mixins.ModelProviderForServiceSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager
import org.joda.time.LocalDateTime
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import spock.lang.Ignore

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.mangofactory.documentation.schema.AlternateTypeRules.*

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport, ServicePluginsSupport])
class OperationParameterReaderSpec extends DocumentationContextSpec {
  OperationParameterReader sut
  def pluginsManager = defaultWebPlugins()
  def setup() {
    def typeResolver = new TypeResolver()
    plugin
            .ignoredParameterTypes(ServletRequest, ServletResponse, HttpServletRequest,
              HttpServletResponse, BindingResult, ServletContext,
              DummyModels.Ignorable.class
            )
            .alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
            .configure(contextBuilder)


    sut = new OperationParameterReader(typeResolver,
            new ModelAttributeParameterExpander(typeResolver, pluginsManager), pluginsManager)
  }
  def "Should support all documentation types"() {
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }


  def "Should ignore ignorables"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

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

  @Ignore("This is an integration test")
  def "Should read a request mapping method without APIParameter annotation"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithSinglePathVariable', String.class)

      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      Parameter parameter = operation.parameters[0]
      assert parameter."$property" == expectedValue
    where:
      property        | expectedValue
      'name'          | 'businessId'
      'description'   | 'businessId'
      'required'      | true
      'allowMultiple' | false
      'allowMultiple' | false
      'paramType'     | "path"
  }

  def "Should expand ModelAttribute request params"() {
    given:
      plugin.directModelSubstitute(LocalDateTime, String)
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithModelAttribute', Example.class), 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.parameters.size() == 8

      Parameter annotatedFooParam = operation.parameters.find { it.name == "foo" }
      annotatedFooParam != null
      annotatedFooParam.getDescription() == null
      !annotatedFooParam.required
      annotatedFooParam.allowableValues == null

      Parameter annotatedBarParam = operation.parameters.find { it.name == "bar" }
      annotatedBarParam.getDescription() == null
      !annotatedBarParam.required
      annotatedBarParam.allowableValues == null

      Parameter unannotatedEnumTypeParam = operation.parameters.find { it.name == "enumType" }
      unannotatedEnumTypeParam.getDescription() == null
      unannotatedEnumTypeParam.allowableValues != null

      Parameter annotatedEnumTypeParam = operation.parameters.find { it.name == "annotatedEnumType" }
      annotatedEnumTypeParam.getDescription() == null
      annotatedEnumTypeParam.allowableValues != null

      Parameter unannotatedNestedTypeNameParam = operation.parameters.find { it.name == "nestedType.name" }
      unannotatedNestedTypeNameParam != null
      unannotatedNestedTypeNameParam.getDescription() == null

      Parameter annotatedAllCapsSetParam = operation.parameters.find { it.name == "allCapsSet" }
      annotatedAllCapsSetParam.getDescription() == null
      !annotatedAllCapsSetParam.required
      annotatedAllCapsSetParam.allowableValues == null

      Parameter unannotatedParentBeanParam = operation.parameters.find { it.name == "parentBeanProperty" }
      unannotatedParentBeanParam.getDescription() == null

      Parameter localDateTime = operation.parameters.find { it.name == "localDateTime" }
      !localDateTime.required
      localDateTime.getDescription() == null
  }

  def "Should expand ModelAttribute request param if param has treeish field"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithTreeishModelAttribute', Treeish.class), 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

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
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.parameters.size() == expectedSize

    where:
      handlerMethod                                                    | expectedSize
      dummyHandlerMethod('methodWithoutModelAttribute', Example.class) | 1
  }

  def "OperationParameterReader supports all documentationTypes"() {
    given:
      def sut = new OperationParameterReader(new TypeResolver(), Mock(ModelAttributeParameterExpander),
              Mock(DocumentationPluginsManager))
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
  }
}
