package com.mangofactory.spring.web.readers
import com.mangofactory.service.model.Parameter
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

@Mixin([RequestMappingSupport])
class OperationParameterRequestConditionReaderSpec extends DocumentationContextSpec {

  OperationParameterRequestConditionReader sut = new OperationParameterRequestConditionReader()
  def "Should read a parameter given a parameter request condition"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=testValue")
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition])
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo,
              context(), "")
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      Parameter parameter = operation.parameters[0]
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
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/parameter-conditions',
              ["paramsCondition": paramCondition])
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo,
              context(), "")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      0 == operation.parameters.size()

  }

  def "Should ignore a parameter request condition expression that is already present in the parameters"() {
    given:
      HandlerMethod handlerMethod = dummyHandlerMethod('methodWithParameterRequestCondition')
      ParamsRequestCondition paramCondition = new ParamsRequestCondition("test=3")
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0,  requestMappingInfo('/parameter-conditions',
                      ["paramsCondition": paramCondition]),
              context(), "/anyPath")

    when:
      OperationParameterRequestConditionReader operationParameterReader = new OperationParameterRequestConditionReader()
      operationParameterReader.apply(operationContext)

    then:
      1 == operationContext.operationBuilder().build().parameters.size()

  }
}
