package com.mangofactory.swagger.readers.operation.parameter
import com.mangofactory.service.model.builder.ParameterBuilder
import com.mangofactory.springmvc.plugins.ParameterContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile

@Mixin([RequestMappingSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
  HandlerMethod handlerMethod = Stub(HandlerMethod)
  MethodParameter methodParameter = Stub(MethodParameter)

   def "Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter,
              defaultValues.typeResolver.resolve(paramType))
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
      methodParameter.getParameterType() >> paramType

    when:
      def sut = new ParameterDataTypeReader(defaultValues.alternateTypeProvider)
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().parameterType == expected
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
