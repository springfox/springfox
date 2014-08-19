package com.mangofactory.swagger.models

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.alternates.AlternateTypeRule
import com.mangofactory.swagger.models.alternates.WildcardType
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.models.ResolvedTypes.asResolved
import static com.mangofactory.swagger.models.alternates.Alternates.newMapRule
import static com.mangofactory.swagger.models.alternates.Alternates.newRule

@Mixin(TypesForTestingSupport)
class AlternateTypeProviderSpec extends Specification {
  def "Alternate types are provided for specified map types"() {
    given:
      AlternateTypeProvider sut = new AlternateTypeProvider()
      sut.addRule(rule)
    expect:
      sut.alternateFor(hashMap(String, String)) == expectedType

    where:
      rule                                   | expectedType
      newMapRule(String, String)             | genericMap(List, String, String)
      newMapRule(WildcardType, String)       | genericMap(List, String, String)
      newMapRule(String, WildcardType)       | genericMap(List, String, String)
      newMapRule(WildcardType, WildcardType) | genericMap(List, String, String)
  }

  @Unroll
  def "Alternate types are provided for specified types"() {
    given:
      def resolver = new TypeResolver()
      def resolvedSource = asResolved(resolver, source)
      AlternateTypeProvider sut = new AlternateTypeProvider()
      sut.addRule(rule)
    expect:
      sut.alternateFor(resolvedSource) == asResolved(resolver, expectedAlternate)

    where:
      rule                                                    | source                          | expectedAlternate
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(SimpleType)  | SimpleType
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(ComplexType) | genericClassOfType(ComplexType)
      newRule(genericClassOfType(WildcardType), SimpleType)   | genericClassOfType(SimpleType)  | SimpleType
      newRule(genericClassOfType(WildcardType), ComplexType)  | genericClassOfType(SimpleType)  | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(SimpleType)  | SimpleType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(ComplexType) | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | ComplexType                     | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | Void                            | Void
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(String)       | String
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(SimpleType)   | SimpleType
      mismatchedNestedGenericRule()                           | nestedGenericType(SimpleType)   | nestedGenericType(nestedGenericType(SimpleType))
      newRule(genericClassOfType(WildcardType), WildcardType) | nestedGenericType(SimpleType)   | resolver.resolve(ResponseEntity, SimpleType)
  }

  private AlternateTypeRule mismatchedNestedGenericRule() {
    newRule(nestedGenericType(WildcardType), nestedGenericType(nestedGenericType(WildcardType)))
  }
}
