package com.mangofactory.swagger.readers.operation

import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.readers.operation.parameter.OperationParameterReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterDataTypeReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterTypeReader
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport,  SpringSwaggerConfigSupport, DocumentationContextSupport])
class OperationImplicitParamsReaderSpec extends Specification {
  Defaults defaultValues = defaults(Mock(ServletContext))
  DocumentationContext context  = defaultContext(Mock(ServletContext))

  def "Should add implicit parameters"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo('/somePath'), handlerMethod)
      OperationParameterReader operationParameterReader = new OperationParameterReader(defaultValues.typeResolver,
              defaultValues.alternateTypeProvider,
              new ParameterDataTypeReader(defaultValues.alternateTypeProvider),
              new ParameterTypeReader(defaultValues.alternateTypeProvider))
      OperationImplicitParametersReader operationImplicitParametersReader = new OperationImplicitParametersReader()
      OperationImplicitParameterReader operationImplicitParameterReader = new OperationImplicitParameterReader()
    when:
      operationParameterReader.execute(context)
      operationImplicitParametersReader.execute(context)
      operationImplicitParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == expectedSize

    where:
      handlerMethod                                                             | expectedSize
      dummyHandlerMethod('dummyMethod')                                         | 0
      dummyHandlerMethod('methodWithApiImplicitParam')                          | 1
      dummyHandlerMethod('methodWithApiImplicitParamAndInteger', Integer.class) | 2
      dummyHandlerMethod('methodWithApiImplicitParams', Integer.class)          | 3
      handlerMethodIn(apiImplicitParamsClass(), 'methodWithApiImplicitParam')   | 2
  }
}
