package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.parameters.Parameter
import org.joda.time.LocalDateTime
import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.web.method.HandlerMethod
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.mangofactory.swagger.models.alternates.Alternates.newRule

@Mixin(RequestMappingSupport)
class OperationParameterReaderSpec extends Specification {

  @Shared
  SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings()
  @Shared
  TypeResolver typeResolver = new TypeResolver()

  def setup() {
    swaggerGlobalSettings.setIgnorableParameterTypes(
            [ServletRequest, ServletResponse, HttpServletRequest,
             HttpServletResponse, BindingResult, ServletContext,
             DummyModels.Ignorable.class
            ] as Set)
    SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()

    swaggerGlobalSettings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider();
  }

  @Unroll
  def "Should ignore ignorables"() {
    given:
      ModelProvider modelProvider = Mock {
        modelFor(_) >> Optional.of(Mock(Model))
      }

      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("modelProvider", modelProvider)

    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
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

      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 1)

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("methodParameter", methodParameter)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      def parameter = result['parameters'][0]
      assert parameter."$property" == expectedValue
    where:
      property           | expectedValue
      'name'             | 'businessId'
      'description'      | 'businessId'
      'required'         | true
      'in'               | "path"
      'collectionFormat' | null //TODO - AK

   }

  def "Should expand ModelAttribute request params"() {
    given:
      swaggerGlobalSettings.alternateTypeProvider.addRule(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))

      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithModelAttribute', Example.class))
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == 8

      Parameter annotatedFooParam = result['parameters'].find { it.name == "foo" }
      annotatedFooParam != null
      annotatedFooParam.description == 'description of foo'
      annotatedFooParam.required
//      annotatedFooParam.allowableValues != null

      Parameter annotatedBarParam = result['parameters'].find { it.name == "bar" }
      annotatedBarParam.description == 'description of bar'
      !annotatedBarParam.required
//      annotatedBarParam.allowableValues == null

      Parameter unannotatedEnumTypeParam = result['parameters'].find { it.name == "enumType" }
      unannotatedEnumTypeParam.description == null
//      unannotatedEnumTypeParam.allowableValues != null

      Parameter annotatedEnumTypeParam = result['parameters'].find { it.name == "annotatedEnumType" }
      annotatedEnumTypeParam.description == 'description of annotatedEnumType'
      //annotatedEnumTypeParam.allowableValues != null

      Parameter unannotatedNestedTypeNameParam = result['parameters'].find { it.name == "nestedType.name" }
      unannotatedNestedTypeNameParam != null
      unannotatedNestedTypeNameParam.description == null

      Parameter annotatedAllCapsSetParam = result['parameters'].find { it.name == "allCapsSet" }
      annotatedAllCapsSetParam.description == 'description of allCapsSet'
      !annotatedAllCapsSetParam.required
      //annotatedAllCapsSetParam.allowableValues == null

      Parameter unannotatedParentBeanParam = result['parameters'].find { it.name == "parentBeanProperty" }
      unannotatedParentBeanParam.description == null

      Parameter localDateTime = result['parameters'].find { it.name == "localDateTime" }
      localDateTime.required
      localDateTime.description == 'local date time desc dd-MM-yyyy hh:mm:ss'
  }

  @Ignore
  def "Should not expand unannotated request params"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == expectedSize

    where:
      handlerMethod                                                    | expectedSize
      dummyHandlerMethod('methodWithoutModelAttribute', Example.class) | 1
  }
}
