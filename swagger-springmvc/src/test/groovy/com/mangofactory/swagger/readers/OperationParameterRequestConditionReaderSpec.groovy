package com.mangofactory.swagger.readers
import com.mangofactory.service.model.Parameter
import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.google.common.collect.Lists.*

@Mixin([RequestMappingSupport, SpringSwaggerConfigSupport, DocumentationContextSupport])
class OperationParameterRequestConditionReaderSpec extends Specification {

  DocumentationContext context  = defaultContext(Mock(ServletContext))
  OperationParameterRequestConditionReader sut = new OperationParameterRequestConditionReader()
  def "Should read a parameter given a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=testValue")
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo
              ('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

      context.put("parameters", newArrayList())
    when:
      sut.execute(context)
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
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo
              ('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

      context.put("parameters", newArrayList())
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      0 == result['parameters'].size()

  }

  def "Should ignore a parameter request condition expression that is already present in the parameters"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=3")
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo
              ('/parameter-conditions',
              ["paramsCondition": paramCondition]),
              handlerMethod)

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
