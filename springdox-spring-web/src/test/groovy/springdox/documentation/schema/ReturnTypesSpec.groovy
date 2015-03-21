package springdox.documentation.schema

import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.type.SimpleType
import spock.lang.Specification
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.spring.web.HandlerMethodReturnTypes
import springdox.documentation.spring.web.dummy.DummyModels
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport

import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport, AlternateTypesSupport])
class ReturnTypesSpec extends Specification {
  TypeNameExtractor sut

  def setup() {
    sut = new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
  }

   def "model types"() {
    expect:
      def type = HandlerMethodReturnTypes.handlerReturnType(new TypeResolver(), handlerMethod)
      println "TYPE: $type"
      type.getErasedType() == expectedType

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

  def "Get response class name from ResolvedType"(){
    expect:
      def namingStrategy = new DefaultGenericTypeNamingStrategy()
      def modelResponseClass = sut.typeName(
              returnValue(new TypeResolver().resolve(GenericType.class, clazz), SWAGGER_12, alternateTypeProvider(),
                      namingStrategy))
      modelResponseClass == expectedResponseClassName

    where:
      clazz       | expectedResponseClassName
      SimpleType  | "GenericType«SimpleType»"
      Integer     | "GenericType«int»"
  }

  def "Cannot instantiate HandlerMethodReturnTypes helper class" () {
    when:
      new HandlerMethodReturnTypes()
    then:
      thrown(UnsupportedOperationException)
  }

}