package com.mangofactory.swagger.models.alternates
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.swagger.models.alternates.WildcardType.*

@Mixin(TypesForTestingSupport)
class WildcardTypeSpec extends Specification {
  def "wildcard matches checks equivalence of types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
    type                               | testType                         || shouldMatch
    hashMap(String, String)            | hashMap(String, Integer)         || false
    hashMap(WildcardType, String)      | hashMap(String, Integer)         || false
    hashMap(String, WildcardType)      | hashMap(Integer, Integer)        || false
    hashMap(String, Integer)           | hashMap(String, String)          || false
    hashMap(String, Integer)           | hashMap(WildcardType, String)    || false
    hashMap(Integer, Integer)          | hashMap(String, WildcardType)    || false
    hashMap(String, String)            | hashMap(String, String)          || true
    hashMap(WildcardType, String)      | hashMap(String, String)          || true
    hashMap(WildcardType, String)      | hashMap(Integer, String)         || true
    hashMap(String, WildcardType)      | hashMap(String, String)          || true
    hashMap(String, WildcardType)      | hashMap(String, Integer)         || true
    hashMap(WildcardType, WildcardType)| hashMap(Integer, String)         || true
  }


  def "wildcard matches checks equivalence of types with nested wildcard types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
      type                               | testType                         || shouldMatch
      nestedGenericType(WildcardType)    | nestedGenericType(String)        || true
      nestedGenericType(String)          | nestedGenericType(WildcardType)  || false
  }
}
