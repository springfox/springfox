package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Specification
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin

import static springfox.documentation.schema.ResolvedTypes.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin(AlternateTypesSupport)
class ModelReferenceProviderSpec extends Specification {
  def "Map of Maps is rendered correctly" () {
    given:
      def modelContext = inputParam(
          TypeWithMapOfMaps,
          DocumentationType.SWAGGER_2,
          alternateTypeProvider(),
          new DefaultGenericTypeNamingStrategy())
      def resolver = new TypeResolver()
      def typeNameExtractor = aTypeNameExtractor(resolver)
    when:
      def sut = modelRefFactory(modelContext, typeNameExtractor)
          .apply(resolver.resolve(
            Map,
            resolver.resolve(String),
            resolver.resolve(Map, String, Foo)))
    then:
      //TODO: Elaborate this test
      sut.itemModel().isPresent()
  }

  def aTypeNameExtractor(TypeResolver resolver) {
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(resolver, modelNameRegistry)
    typeNameExtractor
  }


  class TypeWithMapOfMaps {
    public Map<String, Map<String, Foo>> innerMap;
  }

  class Foo {
    public Integer fooInt;
  }

}
