package com.mangofactory.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.builders.ParameterBuilder
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.service.ResolvedMethodParameter
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.ParameterContext
import com.mangofactory.documentation.spring.web.dummy.DummyModels
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
  HandlerMethod handlerMethod = Stub(HandlerMethod)
  MethodParameter methodParameter = Stub(MethodParameter)
  def typeNameExtractor =
          new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())
  
  ParameterDataTypeReader sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())

  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter,
              new TypeResolver().resolve(paramType))
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
      methodParameter.getParameterType() >> paramType

    when:
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == expected
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
      Byte.class                      | "byte"
      BigDecimal.class                | "double"
      byte.class                      | "byte"
      Boolean.class                   | "boolean"
      boolean.class                   | "boolean"
      Date.class                      | "date-time"
      DummyModels.FunkyBusiness.class | "FunkyBusiness"
      Void.class                      | "Void"
      MultipartFile.class             | "File"
  }

  def "Container Parameter types"() {
    given:
      ResolvedMethodParameter resolvedMethodParameter = Mock(ResolvedMethodParameter)
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
      resolvedMethodParameter.getResolvedParameterType() >> new TypeResolver().resolve(List, String)
      resolvedMethodParameter.getMethodParameter() >> methodParameter
      methodParameter.getParameterType() >> List

    when:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())
      def sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == "List"
      parameterContext.parameterBuilder().build().modelRef.itemType == "string"

  }

}
