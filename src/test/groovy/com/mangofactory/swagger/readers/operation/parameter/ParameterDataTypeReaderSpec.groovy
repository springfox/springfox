package com.mangofactory.swagger.readers.operation.parameter

import com.mangofactory.swagger.configuration.SpringSwaggerModelConfig
import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.joda.time.LocalDateTime
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification
import spock.lang.Unroll

@Mixin(RequestMappingSupport)
class ParameterDataTypeReaderSpec extends Specification {

   @Unroll
   def "Parameter types"() {
    given:
      HandlerMethod handlerMethod = Stub()
      SpringSwaggerModelConfig springSwaggerModelConfig = new SpringSwaggerModelConfig();

      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterType() >> paramType
      context.put("methodParameter", methodParameter)
      Map<Class, String> types = springSwaggerModelConfig.defaultParameterDataTypes()
      types.put(DummyClass.CustomClass, "customClassParamType")
      context.put("parameterDataTypes", types)

    when:
      Command operationCommand = new ParameterDataTypeReader();
      operationCommand.execute(context)
    then:
      context.get('dataType') == expected
    where:
      paramType                      | expected
      char.class                     | "string"
      String.class                   | "string"
      Integer.class                  | "int32"
      int.class                      | "int32"
      Long.class                     | "int64"
      BigInteger.class               | "int64"
      long.class                     | "int64"
      Float.class                    | "float"
      float.class                    | "float"
      Double.class                   | "double"
      double.class                   | "double"
      BigDecimal.class               | "double"
      Byte.class                     | "byte"
      byte.class                     | "byte"
      Boolean.class                  | "boolean"
      boolean.class                  | "boolean"
      Date.class                     | "date-time"
      DummyClass.CustomClass.class   | "customClassParamType"
      SpringSwaggerModelConfig.class | "string"
   }
}
