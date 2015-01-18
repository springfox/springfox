package com.mangofactory.documentation.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.service.model.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander
import com.mangofactory.documentation.spring.web.readers.parameter.OperationParameterReader
import com.mangofactory.documentation.spring.web.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class OperationImplicitParamsReaderSpec extends DocumentationContextSpec {

  def "Should add implicit parameters"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")


      def resolver = new TypeResolver()
      OperationParameterReader operationParameterReader = new OperationParameterReader(resolver,
              new ModelAttributeParameterExpander(resolver, defaultWebPlugins()),
              defaultWebPlugins())
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
