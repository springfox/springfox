package com.mangofactory.swagger.models.alternates
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelProvider
import com.wordnik.swagger.model.Model
import spock.lang.Specification

import static com.mangofactory.swagger.models.ModelContext.*

@Mixin([ModelProviderSupport, TypesForTestingSupport])
class AlternatePropertiesSpec extends Specification {
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesLocalDateWithString()
      Model model = modelProvider.modelFor(inputParam(typeWithAlternateProperty())).get()
    expect:
      model.name() == "TypeWithAlternateProperty"
      model.properties().contains("localDate")
      def modelProperty = model.properties().get("localDate")
      modelProperty.get().type() == "string"
      modelProperty.get().qualifiedType() == "java.lang.String"
      modelProperty.get().items().isEmpty()
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesResponseEntityOfVoid()
      Model model = modelProvider.modelFor(inputParam(typeWithResponseEntityOfVoid())).get()
    expect:
      model.name() == "GenericType«ResponseEntity«Void»»"
      model.properties().contains("genericField")
      def modelProperty = model.properties().get("genericField")
      modelProperty.get().type() == "Void"
      modelProperty.get().qualifiedType() == "java.lang.Void"
      modelProperty.get().items().isEmpty()
  }
}
