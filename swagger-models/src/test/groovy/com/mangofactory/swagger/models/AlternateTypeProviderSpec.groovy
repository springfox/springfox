package com.mangofactory.swagger.models

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.alternates.WildcardType
import spock.lang.Specification

import static com.mangofactory.swagger.models.ResolvedTypes.asResolved
import static com.mangofactory.swagger.models.alternates.Alternates.*

@Mixin(TypesForTestingSupport)
class AlternateTypeProviderSpec extends Specification {
  def "Alternate types are provided for specified map types" () {
    given:
      AlternateTypeProvider sut = new AlternateTypeProvider()
      sut.addRule(rule)
    expect:
      sut.alternateFor(hashMap(String, String))  == expectedType

    where:
    rule                                        | expectedType
    hashMapAlternate(String, String)            | genericMap(List, String, String)
    hashMapAlternate(WildcardType, String)      | genericMap(List, String, String)
    hashMapAlternate(String, WildcardType)      | genericMap(List, String, String)
    hashMapAlternate(WildcardType, WildcardType)| genericMap(List, String, String)
  }

  def "Alternate types are provided for specified types" () {
    given:
      def resolvedSource = asResolved(new TypeResolver(), source)
      AlternateTypeProvider sut = new AlternateTypeProvider()
      sut.addRule(rule)
    expect:
      sut.alternateFor(resolvedSource).erasedType == expectedAlternate

    where:
    rule                                                    | source                            | expectedAlternate
    newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(SimpleType)    | SimpleType
    newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(ComplexType)   | genericClassOfType(ComplexType).erasedType
    newRule(genericClassOfType(WildcardType), SimpleType)   | genericClassOfType(SimpleType)    | SimpleType
    newRule(genericClassOfType(WildcardType), ComplexType)  | genericClassOfType(SimpleType)    | ComplexType
    newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(SimpleType)    | SimpleType
    newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(ComplexType)   | ComplexType
    newRule(genericClassOfType(WildcardType), WildcardType) | ComplexType                       | ComplexType
  }
}
