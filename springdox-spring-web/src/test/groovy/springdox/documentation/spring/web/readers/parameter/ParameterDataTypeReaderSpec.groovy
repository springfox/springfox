package springdox.documentation.spring.web.readers.parameter
import com.fasterxml.classmate.TypeResolver
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile
import springdox.documentation.builders.ParameterBuilder
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.service.ResolvedMethodParameter
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.service.contexts.ParameterContext
import springdox.documentation.spring.web.dummy.DummyModels
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class ParameterDataTypeReaderSpec extends DocumentationContextSpec {
  HandlerMethod handlerMethod = Stub(HandlerMethod)
  MethodParameter methodParameter = Stub(MethodParameter)
  def typeNameExtractor =
          new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
  
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
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy)
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
    def namingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext =
              new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), namingStrategy)
      resolvedMethodParameter.getResolvedParameterType() >> new TypeResolver().resolve(List, String)
      resolvedMethodParameter.getMethodParameter() >> methodParameter
      methodParameter.getParameterType() >> List

    when:
      def typeNameExtractor =
              new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
      def sut = new ParameterDataTypeReader(typeNameExtractor, new TypeResolver())
      sut.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().modelRef.type == "List"
      parameterContext.parameterBuilder().build().modelRef.itemType == "string"

  }

}
