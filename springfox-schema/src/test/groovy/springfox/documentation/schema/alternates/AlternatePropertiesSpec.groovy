package springfox.documentation.schema.alternates
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import springfox.documentation.schema.*
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.AlternateTypeRule
import springfox.documentation.schema.AlternateTypesSupport
import springfox.documentation.schema.Model
import springfox.documentation.schema.ModelProvider
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.schema.AlternateTypeRules.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([ModelProviderSupport, TypesForTestingSupport, AlternateTypesSupport])
class AlternatePropertiesSpec extends Specification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      def provider = alternateTypeProvider()
      provider.addRule(newRule(LocalDate, String))
      ModelProvider modelProvider = defaultModelProvider()
      Model model = modelProvider.modelFor(inputParam(typeWithAlternateProperty(), SWAGGER_12, provider, namingStrategy)).get()
    expect:
      model.getName() == "TypeWithAlternateProperty"
      model.getProperties().containsKey("localDate")
      def modelProperty = model.getProperties().get("localDate")
      modelProperty.type.erasedType == String
      modelProperty.getQualifiedType() == "java.lang.String"
      def item = modelProperty.getModelRef()
      item.type == "string"
      !item.collection
      item.itemType == null
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided" () {
    given:
      def provider = alternateTypeProvider()
      provider.addRule(new AlternateTypeRule(resolver.resolve(ResponseEntity, Void), resolver.resolve(Void)))
      ModelProvider modelProvider = defaultModelProvider()
      Model model = modelProvider.modelFor(inputParam(typeWithResponseEntityOfVoid(), SWAGGER_12,
              alternateTypeProvider(), namingStrategy)).get()
    expect:
      model.getName() == "GenericType«ResponseEntity«Void»»"
      model.getProperties().containsKey("genericField")
      def modelProperty = model.getProperties().get("genericField")
      modelProperty.type.erasedType == Void
      modelProperty.getQualifiedType() == "java.lang.Void"
      def item = modelProperty.getModelRef()
      item.type == "Void"
      !item.collection
      item.itemType == null
  }
}
