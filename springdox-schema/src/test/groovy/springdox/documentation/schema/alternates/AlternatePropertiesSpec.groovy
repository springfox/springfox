package springdox.documentation.schema.alternates
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import springdox.documentation.schema.*
import springdox.documentation.schema.mixins.ModelProviderSupport
import springdox.documentation.schema.mixins.TypesForTestingSupport

import static springdox.documentation.schema.AlternateTypeRules.*
import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

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
