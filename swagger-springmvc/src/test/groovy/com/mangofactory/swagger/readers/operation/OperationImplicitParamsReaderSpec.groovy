package com.mangofactory.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.readers.operation.parameter.OperationParameterReader
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Shared
import spock.lang.Specification

import static com.google.common.collect.Maps.newHashMap

@Mixin(RequestMappingSupport)
class OperationImplicitParamsReaderSpec extends Specification {

  @Shared SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings()

  def setup() {
    SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
    swaggerGlobalSettings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver());
    swaggerGlobalSettings.setGlobalResponseMessages(newHashMap())
  }

  def "Should add implicit parameters"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      OperationParameterReader operationParameterReader = new OperationParameterReader()
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
