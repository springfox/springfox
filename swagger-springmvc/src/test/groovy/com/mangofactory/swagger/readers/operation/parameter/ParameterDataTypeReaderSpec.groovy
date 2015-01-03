package com.mangofactory.swagger.readers.operation.parameter

import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile

@Mixin([RequestMappingSupport, DocumentationContextSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
   def "Parameter types"() {
    given:
      HandlerMethod handlerMethod = Stub(HandlerMethod)

      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter,
              defaultValues.typeResolver.resolve(paramType))
      methodParameter.getParameterType() >> paramType
      context.put("methodParameter", methodParameter)
      context.put("resolvedMethodParameter", resolvedMethodParameter)

    when:
      Command sut = new ParameterDataTypeReader(defaultValues.alternateTypeProvider)
      sut.execute(context)
    then:
      context.get('dataType') == expected
    where:
      paramType                       | expected
      char.class                      | "string"
      String.class                    | "string"
      Integer.class                   | "int"
      int.class                       | "int"
      Long.class                      | "long"
      BigInteger.class                | "long"
      long.class                      | "long"
      Float.class                     | "float"
      float.class                     | "float"
      Double.class                    | "double"
      double.class                    | "double"
      BigDecimal.class                | "double"
      Byte.class                      | "byte"
      byte.class                      | "byte"
      Boolean.class                   | "boolean"
      boolean.class                   | "boolean"
      Date.class                      | "date-time"
      DummyModels.FunkyBusiness.class | "FunkyBusiness"
      Void.class                      | "Void"
      MultipartFile.class             | "File"
   }

}
