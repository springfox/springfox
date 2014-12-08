package com.mangofactory.swagger.readers

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.models.dto.Parameter
import org.springframework.validation.BindingResult
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.google.common.collect.Lists.newArrayList
import static com.google.common.collect.Maps.newHashMap

@Mixin(RequestMappingSupport)
class OperationParameterRequestConditionReaderTest extends Specification {

  @Shared
  SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings()

  def setup() {
    swaggerGlobalSettings.setIgnorableParameterTypes([ServletRequest, ServletResponse, HttpServletRequest,
                                                      HttpServletResponse, BindingResult, ServletContext, DummyModels.Ignorable.class] as Set)
    SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
    swaggerGlobalSettings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver());
    swaggerGlobalSettings.setGlobalResponseMessages(newHashMap())
  }

  def "Should read a parameter given a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=testValue")
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("parameters", newArrayList())
    when:
      OperationParameterRequestConditionReader operationParameterReader = new OperationParameterRequestConditionReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Parameter parameter = result['parameters'][0]
      assert parameter."$property" == expectedValue
    where:
      property        | expectedValue
      'name'          | 'test'
      'description'   | null
      'required'      | true
      'allowMultiple' | false
      'paramType'     | "query"

  }

  def "Should ignore a negated parameter in a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("!test")
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("parameters", newArrayList())
    when:
      OperationParameterRequestConditionReader operationParameterReader = new OperationParameterRequestConditionReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      0 == result['parameters'].size()

  }

  def "Should ignore a parameter request condition expression that is already present in the parameters"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=3")
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)

      def parameter = new Parameter("test", null, "", true, false, "string", null, "string", "")
      context.put("parameters", newArrayList(parameter))
    when:
      OperationParameterRequestConditionReader operationParameterReader = new OperationParameterRequestConditionReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      1 == result['parameters'].size()

  }
}
