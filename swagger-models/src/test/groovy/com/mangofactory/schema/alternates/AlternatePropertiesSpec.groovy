package com.mangofactory.schema.alternates
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.schema.ModelProvider
import com.mangofactory.service.model.Model
import spock.lang.Specification

import static com.mangofactory.schema.ModelContext.*

@Mixin([ModelProviderSupport, TypesForTestingSupport])
class AlternatePropertiesSpec extends Specification {
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesLocalDateWithString()
      Model model = modelProvider.modelFor(inputParam(typeWithAlternateProperty(), documentationType())).get()
    expect:
      model.getName() == "TypeWithAlternateProperty"
      model.getProperties().containsKey("localDate")
      def modelProperty = model.getProperties().get("localDate")
      modelProperty.typeName() == "string"
      modelProperty.getQualifiedType() == "java.lang.String"
      modelProperty.getItems() == null
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesResponseEntityOfVoid()
      Model model = modelProvider.modelFor(inputParam(typeWithResponseEntityOfVoid(), documentationType())).get()
    expect:
      model.getName() == "GenericType«ResponseEntity«Void»»"
      model.getProperties().containsKey("genericField")
      def modelProperty = model.getProperties().get("genericField")
      modelProperty.typeName() == "Void"
      modelProperty.getQualifiedType() == "java.lang.Void"
      modelProperty.getItems() == null
  }
}
