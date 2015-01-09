package com.mangofactory.spring.web.readers.operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.PluginsSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationImplicitParameterReader
import com.mangofactory.swagger.plugins.operation.OperationImplicitParametersReader
import com.mangofactory.spring.web.readers.operation.parameter.ModelAttributeParameterExpander
import com.mangofactory.spring.web.readers.operation.parameter.OperationParameterReader
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport, PluginsSupport])
class OperationImplicitParamsReaderSpec extends DocumentationContextSpec {

  def "Should add implicit parameters"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      def alternateTypeProvider = defaultValues.alternateTypeProvider
      def typeResolver = defaultValues.typeResolver
      OperationParameterReader operationParameterReader = new OperationParameterReader(typeResolver,
              new ModelAttributeParameterExpander(alternateTypeProvider, typeResolver, springPluginsManager()),
              springPluginsManager())
      OperationImplicitParametersReader operationImplicitParametersReader = new OperationImplicitParametersReader()
      OperationImplicitParameterReader operationImplicitParameterReader = new OperationImplicitParameterReader()
    when:
      operationParameterReader.apply(operationContext)
      operationImplicitParametersReader.apply(operationContext)
      operationImplicitParameterReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()
    then:
      operation.parameters.size() == expectedSize

    where:
      handlerMethod                                                             | expectedSize
      dummyHandlerMethod('dummyMethod')                                         | 0
      dummyHandlerMethod('methodWithApiImplicitParam')                          | 1
      dummyHandlerMethod('methodWithApiImplicitParamAndInteger', Integer.class) | 2
      dummyHandlerMethod('methodWithApiImplicitParams', Integer.class)          | 3
      handlerMethodIn(apiImplicitParamsClass(), 'methodWithApiImplicitParam')   | 2
  }
}
