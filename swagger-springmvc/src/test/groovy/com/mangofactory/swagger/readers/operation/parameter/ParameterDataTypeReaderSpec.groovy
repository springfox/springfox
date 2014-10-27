package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.models.ResolvedTypes.*

@Mixin(RequestMappingSupport)
class ParameterDataTypeReaderSpec extends Specification {

  @Unroll("Should resolve [#paramType] to [#expected]")
   def "Parameter types"() {
    given:
      HandlerMethod handlerMethod = Stub(HandlerMethod)

      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolve(paramType))
      methodParameter.getParameterType() >> paramType
      context.put("methodParameter", methodParameter)
      context.put("resolvedMethodParameter", resolvedMethodParameter)

      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings()
      SwaggerModelsConfiguration swaggerModelConfig = new SwaggerModelsConfiguration()
      swaggerGlobalSettings.alternateTypeProvider = swaggerModelConfig.alternateTypeProvider();
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)

    when:
      Command operationCommand = new ParameterDataTypeReader();
      operationCommand.execute(context)
    then:
      context.get('dataType') == expected
    where:
      paramType                       | expected
      char.class                      | "string"
      String.class                    | "string"
      Integer.class                   | "integer"
      int.class                       | "integer"
      Long.class                      | "integer"
      BigInteger.class                | "integer" //64b
      long.class                      | "integer"
      Float.class                     | "number"
      float.class                     | "number"
      Double.class                    | "number"
      double.class                    | "number"
      BigDecimal.class                | "number"
      Byte.class                      | "string"
      byte.class                      | "string" //byte
      Boolean.class                   | "boolean"
      boolean.class                   | "boolean"
      Date.class                      | "string"
//      DummyClass.CustomClass.class    | "customClassParamType" //DK TODO: Alternate types
      DummyModels.FunkyBusiness.class | "FunkyBusiness"
      Void.class                      | "Void"
      MultipartFile.class             | "file"
   }

  ResolvedType resolve(Class clazz) {
    asResolved(new TypeResolver(), clazz);
  }
}
