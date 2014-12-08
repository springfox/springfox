package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.dummy.models.Treeish
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.models.dto.Parameter
import org.joda.time.LocalDateTime
import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.web.method.HandlerMethod
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.google.common.collect.Maps.newHashMap
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

    swaggerGlobalSettings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(typeResolver);
    swaggerGlobalSettings.setGlobalResponseMessages(newHashMap())
  }

  def "Should ignore ignorables"() {
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
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithTreeishModelAttribute', Treeish.class))
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == 1

      Parameter annotatedBarParam = result['parameters'][0]
      annotatedBarParam != null
      annotatedBarParam.name == 'example'
  }

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
