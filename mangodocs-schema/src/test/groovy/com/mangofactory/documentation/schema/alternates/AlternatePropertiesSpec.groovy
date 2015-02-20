package com.mangofactory.documentation.schema.alternates
import com.mangofactory.documentation.schema.AlternateTypeRule
import com.mangofactory.documentation.schema.AlternateTypesSupport
import com.mangofactory.documentation.schema.Model
import com.mangofactory.documentation.schema.ModelProvider
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static com.mangofactory.documentation.schema.AlternateTypeRules.*
import static com.mangofactory.documentation.spi.DocumentationType.*
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([ModelProviderSupport, TypesForTestingSupport, AlternateTypesSupport])
class AlternatePropertiesSpec extends Specification {
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      def provider = alternateTypeProvider()
      provider.addRule(newRule(LocalDate, String))
      ModelProvider modelProvider = defaultModelProvider()
      Model model = modelProvider.modelFor(inputParam(typeWithAlternateProperty(), SWAGGER_12, provider)).get()
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
              alternateTypeProvider())).get()
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
