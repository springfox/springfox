package springfox.documentation.spring.web.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spring.web.dummy.models.Example

import static springfox.documentation.spring.web.readers.operation.ModelRefs.*

class ModelRefsSpec extends Specification {
  @Shared TypeResolver resolver = new TypeResolver()

  def "Cannot instantiate constructor"() {
    when:
      new ModelRefs()
    then:
      thrown(UnsupportedOperationException)
  }

  @Unroll
  def "Creates model refs based on resolved #type"() {
    given:
      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
      def nameExtractor = new TypeNameExtractor(resolver, modelNameRegistry)
      def modelContext = ModelContext.inputParam(type, DocumentationType.SPRING_WEB,
          new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())
    when:
      def sut = modelRef(Optional.of(type), modelContext, nameExtractor).get()
    then:
      sut.type.equals(refType)
      sut.itemType?.equals(refItemType) || refItemType == null
    where:
      type                                   | refType     | refItemType
      resolver.resolve(List, String)         | "List"      | "string"
      resolver.resolve(List, Example)        | "List"      | "Example"
      resolver.resolve(Set, String)          | "Set"       | "string"
      resolver.resolve(Set, Example)         | "Set"       | "Example"
      resolver.arrayType(String)             | "Array"     | "string"
      resolver.arrayType(Example)            | "Array"     | "Example"
      resolver.resolve(String)               | "string"    | null
      resolver.resolve(Example)              | "Example"   | null
      resolver.resolve(Void)                 | "void"      | null
      resolver.resolve(Map, String, String)  | "Map"       | "string"
      resolver.resolve(Map, String, Example) | "Map"       | "Example"
  }

  def "Null types create null model refs"() {
    given:
      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
          OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
      def nameExtractor = new TypeNameExtractor(resolver, modelNameRegistry)
      def modelContext = ModelContext.inputParam(null, DocumentationType.SPRING_WEB,
          new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())

    when:
      def sut = modelRef(Optional.absent(), modelContext, nameExtractor)

    then:
      !sut.isPresent()
  }
}
