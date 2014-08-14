package com.mangofactory.swagger.readers.operation.parameter
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.Parameter
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
import static com.mangofactory.swagger.ScalaUtils.toOption

@Mixin(RequestMappingSupport)
class OperationParameterReaderSpec extends Specification {

   @Shared SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings()

   def setup() {
      swaggerGlobalSettings.setIgnorableParameterTypes([ServletRequest, ServletResponse, HttpServletRequest,
          HttpServletResponse, BindingResult, ServletContext, DummyModels.Ignorable.class] as Set)
      SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
      swaggerGlobalSettings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver());
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
      'description'   | toOption('businessId')
      'required'      | true
      'allowMultiple' | false
      'allowMultiple' | false
      'paramType'     | "path"

   }

   def "Should expand ModelAttribute request params"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), 
        dummyHandlerMethod('methodWithModelAttribute', Example.class))
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == 6
      
      Parameter annotatedFooParam = result['parameters'][0]
      annotatedFooParam != null
      annotatedFooParam.name == 'foo'
      annotatedFooParam.description().get() == 'description of foo'
      annotatedFooParam.required
      annotatedFooParam.allowableValues != null
      
      Parameter annotatedBarParam = result['parameters'][1]
      annotatedBarParam != null
      annotatedBarParam.name == 'bar'
      annotatedBarParam.description().get() == 'description of bar'
      !annotatedBarParam.required
      annotatedBarParam.allowableValues == null
      
      Parameter unannotatedEnumTypeParam = result['parameters'][2]
      unannotatedEnumTypeParam != null
      unannotatedEnumTypeParam.name == 'enumType'
      unannotatedEnumTypeParam.description().isEmpty()
      unannotatedEnumTypeParam.allowableValues != null
      
      Parameter annotatedEnumTypeParam = result['parameters'][3]
      annotatedEnumTypeParam != null
      annotatedEnumTypeParam.name == 'annotatedEnumType'
      annotatedEnumTypeParam.description().get() == 'description of annotatedEnumType'
      annotatedEnumTypeParam.allowableValues != null
      
      Parameter unannotatedNestedTypeNameParam = result['parameters'][4]
      unannotatedNestedTypeNameParam != null
      unannotatedNestedTypeNameParam.name == 'nestedType.name'
      unannotatedNestedTypeNameParam.description().isEmpty()
      
      Parameter unannotatedParentBeanParam = result['parameters'][5]
      unannotatedParentBeanParam != null
      unannotatedParentBeanParam.name == 'parentBeanProperty'
      unannotatedParentBeanParam.description().isEmpty()
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
