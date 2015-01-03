package com.mangofactory.swagger.readers.operation.parameter
import com.mangofactory.service.model.Parameter
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.dummy.models.Treeish
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.ModelProviderForServiceSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.joda.time.LocalDateTime
import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.web.method.HandlerMethod

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.mangofactory.schema.alternates.Alternates.*

@Mixin([RequestMappingSupport, DocumentationContextSupport, ModelProviderForServiceSupport])
class OperationParameterReaderSpec extends DocumentationContextSpec {
  OperationParameterReader sut
  def setup() {
    def typeResolver = defaultValues.typeResolver
    plugin
            .ignoredParameterTypes(ServletRequest, ServletResponse, HttpServletRequest,
              HttpServletResponse, BindingResult, ServletContext,
              DummyModels.Ignorable.class
            )
            .alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
            .build(defaultContextBuilder(defaultValues))

    sut = new OperationParameterReader(typeResolver, defaultValues
            .alternateTypeProvider,
            new ParameterDataTypeReader(defaultValues.alternateTypeProvider),
            new ParameterTypeReader(defaultValues.alternateTypeProvider))
  }

  def "Should ignore ignorables"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              handlerMethod)
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == expectedSize

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

      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              handlerMethod)
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 1)

      context.put("methodParameter", methodParameter)
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Parameter parameter = result['parameters'][0]
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
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithModelAttribute', Example.class))
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == 8

      Parameter annotatedFooParam = result['parameters'].find { it.name == "foo" }
      annotatedFooParam != null
      annotatedFooParam.getDescription() == 'description of foo'
      annotatedFooParam.required
      annotatedFooParam.allowableValues != null

      Parameter annotatedBarParam = result['parameters'].find { it.name == "bar" }
      annotatedBarParam.getDescription() == 'description of bar'
      !annotatedBarParam.required
      annotatedBarParam.allowableValues == null

      Parameter unannotatedEnumTypeParam = result['parameters'].find { it.name == "enumType" }
      unannotatedEnumTypeParam.getDescription() == null
      unannotatedEnumTypeParam.allowableValues != null

      Parameter annotatedEnumTypeParam = result['parameters'].find { it.name == "annotatedEnumType" }
      annotatedEnumTypeParam.getDescription() == 'description of annotatedEnumType'
      annotatedEnumTypeParam.allowableValues != null

      Parameter unannotatedNestedTypeNameParam = result['parameters'].find { it.name == "nestedType.name" }
      unannotatedNestedTypeNameParam != null
      unannotatedNestedTypeNameParam.getDescription() == null

      Parameter annotatedAllCapsSetParam = result['parameters'].find { it.name == "allCapsSet" }
      annotatedAllCapsSetParam.getDescription() == 'description of allCapsSet'
      !annotatedAllCapsSetParam.required
      annotatedAllCapsSetParam.allowableValues == null

      Parameter unannotatedParentBeanParam = result['parameters'].find { it.name == "parentBeanProperty" }
      unannotatedParentBeanParam.getDescription() == null

      Parameter localDateTime = result['parameters'].find { it.name == "localDateTime" }
      localDateTime.required
      localDateTime.getDescription() == 'local date time desc dd-MM-yyyy hh:mm:ss'
  }

  def "Should expand ModelAttribute request param if param has treeish field"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithTreeishModelAttribute', Treeish.class))
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == 1

      Parameter annotatedBarParam = result['parameters'][0]
      annotatedBarParam != null
      annotatedBarParam.name == 'example'
  }

  def "Should not expand unannotated request params"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo('/somePath'),
      handlerMethod)
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == expectedSize

    where:
      handlerMethod                                                    | expectedSize
      dummyHandlerMethod('methodWithoutModelAttribute', Example.class) | 1
  }

}
