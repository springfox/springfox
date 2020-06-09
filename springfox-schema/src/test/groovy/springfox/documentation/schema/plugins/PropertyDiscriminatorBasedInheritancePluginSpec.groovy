package springfox.documentation.schema.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.property.ModelSpecificationFactory
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext

import java.lang.reflect.Type

import static java.util.Collections.*

class PropertyDiscriminatorBasedInheritancePluginSpec extends Specification {
  def resolver = new TypeResolver()

  def "Supports all plugins"() {
    given:
    def sut =
        new PropertyDiscriminatorBasedInheritancePlugin(
            resolver,
            Mock(EnumTypeDeterminer),
            Mock(TypeNameExtractor),
            Mock(ModelSpecificationFactory))

    expect:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "Types with subtype info for #type.name"() {
    given:
    def sut =
        new PropertyDiscriminatorBasedInheritancePlugin(
            resolver,
            enumTypeDeterminer(),
            typeNameExtractor(),
            new ModelSpecificationFactory(typeNameExtractor(), enumTypeDeterminer()))
    def context = modelContext(type)

    when:
    sut.apply(context)
    def built = context.builder.build()

    then:
    built.discriminator == discriminator
    built.subTypes.size() == subTypes.size()

    where:
    type | discriminator | subTypes
    A1   | null          | []
    A2   | null          | []
    A3   | null          | []
    A4   | "type"        | [B1, B2]
    A5   | "@type"       | [B3, B4]

  }

  class A1 {

  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "test"
  )
  class A2 {

  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.CLASS,
      include = JsonTypeInfo.As.PROPERTY,
      property = "test"
  )
  class A3 {

  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY,
      property = "type"
  )
  @JsonSubTypes([
    @JsonSubTypes.Type(value = B1, name = "b1"),
    @JsonSubTypes.Type(value = B2, name = "b2")
  ])
  abstract class A4 {
    def type

    abstract def getType()
  }

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.PROPERTY
  )
  @JsonSubTypes([
    @JsonSubTypes.Type(value = B3, name = "b1"),
    @JsonSubTypes.Type(value = B4, name = "b2")
  ])
  abstract class A5 {

    @JsonProperty("@type")
    abstract def getType()
  }


  class B1 extends A4 {

    @Override
    def getType() {
      return "b1"
    }
  }

  class B2 extends A4 {

    @Override
    def getType() {
      return "b2"
    }
  }

  class B3 extends A5 {

    @Override
    def getType() {
      return "b1"
    }
  }

  class B4 extends A5 {

    @Override
    def getType() {
      return "b2"
    }
  }

  ModelContext modelContext(Type type) {
    ModelContext.inputParam(
        "0_0",
        "test",
        resolver.resolve(type),
        Optional.empty(),
        new HashSet<>(),
        DocumentationType.SWAGGER_2,
        new AlternateTypeProvider([]),
        new DefaultGenericTypeNamingStrategy(),
        emptySet()
    )
  }

  def typeNameExtractor() {
    new TypeNameExtractor(resolver, modelNamePlugins(), new JacksonEnumTypeDeterminer())
  }
  
  def enumTypeDeterminer() {
    new JacksonEnumTypeDeterminer()
  }

  PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNamePlugins() {
    OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
  }
}
