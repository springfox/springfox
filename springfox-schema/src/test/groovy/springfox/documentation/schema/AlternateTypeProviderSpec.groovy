/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.schema
import com.fasterxml.classmate.TypeResolver
import org.springframework.hateoas.CollectionModel
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.service.contexts.Defaults

import static springfox.documentation.schema.AlternateTypeRules.*

@Mixin(TypesForTestingSupport)
class AlternateTypeProviderSpec extends Specification {
  def "Alternate types are provided for specified map types"() {
    given:
      AlternateTypeProvider sut = new AlternateTypeProvider([])
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
      def resolvedSource = resolver.resolve(source)
      AlternateTypeProvider sut = new AlternateTypeProvider([])
      sut.addRule(rule)
    expect:
      sut.alternateFor(resolvedSource) == resolver.resolve(expectedAlternate)

    where:
      rule                                                    | source                           | expectedAlternate
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(ComplexType)  | genericClassOfType(ComplexType)
      newRule(genericClassOfType(WildcardType), SimpleType)   | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(WildcardType), ComplexType)  | genericClassOfType(SimpleType)   | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(ComplexType)  | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | ComplexType                      | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | Void                             | Void
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(String)        | String
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(SimpleType)    | SimpleType
      mismatchedNestedGenericRule()                           | nestedGenericType(SimpleType)    | nestedGenericType(nestedGenericType(SimpleType))
      newRule(genericClassOfType(WildcardType), WildcardType) | nestedGenericType(SimpleType)    | resolver.resolve(ResponseEntity, SimpleType)
      hateoasCollectionModelRule()                            | resources(SimpleTypeEntityModel) | resolver.resolve(List, SimpleType)
  }

  @Unroll
  def "Alternate types are provided for specified types when default rules are applied"() {
    given:
      def resolver = new TypeResolver()
      def resolvedSource = resolver.resolve(source)
      def sut = new AlternateTypeProvider(new Defaults().defaultRules(resolver))
      sut.addRule(rule)
    expect:
      sut.alternateFor(resolvedSource) == resolver.resolve(expectedAlternate)

    where:
      rule                                                    | source                           | expectedAlternate
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(SimpleType), SimpleType)     | genericClassOfType(ComplexType)  | genericClassOfType(ComplexType)
      newRule(genericClassOfType(WildcardType), SimpleType)   | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(WildcardType), ComplexType)  | genericClassOfType(SimpleType)   | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(SimpleType)   | SimpleType
      newRule(genericClassOfType(WildcardType), WildcardType) | genericClassOfType(ComplexType)  | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | ComplexType                      | ComplexType
      newRule(genericClassOfType(WildcardType), WildcardType) | Void                             | Void
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(String)        | String
      newRule(nestedGenericType(WildcardType), WildcardType)  | nestedGenericType(SimpleType)    | SimpleType
      mismatchedNestedGenericRule()                           | nestedGenericType(SimpleType)    | nestedGenericType(nestedGenericType(SimpleType))
      newRule(genericClassOfType(WildcardType), WildcardType) | nestedGenericType(SimpleType)    | resolver.resolve(ResponseEntity, SimpleType)
      hateoasCollectionModelRule()                            | resources(SimpleTypeEntityModel) | resolver.resolve(List, SimpleType)
  }

  AlternateTypeRule hateoasCollectionModelRule() {
    def typeResolver = new TypeResolver()
    newRule(
        typeResolver.resolve(CollectionModel.class, SimpleTypeEntityModel.class),
        typeResolver.resolve(List.class, SimpleType.class))
  }

  private AlternateTypeRule mismatchedNestedGenericRule() {
    newRule(nestedGenericType(WildcardType), nestedGenericType(nestedGenericType(WildcardType)))
  }
}
