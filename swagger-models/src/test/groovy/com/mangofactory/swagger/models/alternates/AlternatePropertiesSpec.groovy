package com.mangofactory.swagger.models.alternates
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.ModelProvider
import com.wordnik.swagger.models.ModelImpl
import spock.lang.Specification

import static com.mangofactory.swagger.models.ModelContext.inputParam

@Mixin([ModelProviderSupport, TypesForTestingSupport])
class AlternatePropertiesSpec extends Specification {
  def "Nested properties that have alternate types defined are rendered correctly" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesLocalDateWithString()
      ModelImpl model = modelProvider.modelFor(inputParam(typeWithAlternateProperty())).get()
    expect:
      model.name == "TypeWithAlternateProperty"
      model.properties.localDate
      def modelProperty = model.properties.localDate
      modelProperty.type == "string"
  }

  def "ResponseEntity«Void» renders correctly when an alternate type is provided" () {
    given:
      ModelProvider modelProvider = providerThatSubstitutesResponseEntityOfVoid()
      ModelImpl model = modelProvider.modelFor(inputParam(typeWithResponseEntityOfVoid())).get()
    expect:
      model.name == "GenericType«ResponseEntity«Void»»"
      model.properties.genericField != null
      def modelProperty = model.properties.genericField
      modelProperty.ref == "Void"
  }
}
