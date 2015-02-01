package com.mangofactory.documentation.schema
import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Type

import static com.mangofactory.documentation.schema.WildcardType.*

class WildcardTypeSpec extends Specification {
  @Shared TypeResolver resolver = new TypeResolver()
  def "Cannot instantiate the wildcardtype class" () {
    when:
      new WildcardType()
    then:
      thrown(UnsupportedOperationException)
  }

  def "wildcard matches checks equivalence of types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
    type                               | testType                         | shouldMatch
    hashMap(String, String)            | hashMap(String, Integer)         | false
    hashMap(WildcardType, String)      | hashMap(String, Integer)         | false
    hashMap(String, WildcardType)      | hashMap(Integer, Integer)        | false
    hashMap(String, Integer)           | hashMap(String, String)          | false
    hashMap(String, Integer)           | hashMap(WildcardType, String)    | false
    hashMap(Integer, Integer)          | hashMap(String, WildcardType)    | false
    hashMap(String, String)            | hashMap(String, String)          | true
    hashMap(WildcardType, String)      | hashMap(String, String)          | true
    hashMap(WildcardType, String)      | hashMap(Integer, String)         | true
    hashMap(String, WildcardType)      | hashMap(String, String)          | true
    hashMap(String, WildcardType)      | hashMap(String, Integer)         | true
    hashMap(WildcardType, WildcardType)| hashMap(Integer, String)         | true
  }


  def "wildcard matches checks equivalence of types with nested wildcard types" () {
    given:
    expect:
      wildcardMatch(testType, type) == shouldMatch

    where:
      type                               | testType                         | shouldMatch
      nestedGenericType(WildcardType)    | nestedGenericType(String)        | true
      nestedGenericType(String)          | nestedGenericType(WildcardType)  | false
      resolve(List, WildcardType)        | resolve(ArrayList, String)       | false
  }

  def "wildcard matches fail when the type bindings are not of the same size" () {
    given:
    expect:
      !wildcardMatch(testType, type)

    where:
      type                               | testType
      nestedGenericType(WildcardType)    | resolve(String)
      resolve(List, WildcardType)        | resolve(String)
  }


  def "Replacing wild cards throws an exception when the replaceables is empty" () {
    given:
      Iterable<ResolvedType> replaceables = []
    when:
      replaceWildcardsFrom(replaceables, resolver.resolve(WildcardType))
    then:
      thrown(IllegalStateException)
  }

  def "Collecting replaceables works on nested types" () {
    given:
      def wildcardtype = resolve(List, resolve(List, WildcardType))
      def replacingType = resolve(List, resolve(List, String))
    when:
      def replacables = collectReplaceables(replacingType, wildcardtype)
    then:
      replacables.size() == 1
  }

  def "Replacing wild cards throws an exception when the number of replaceables don't match the wildcards in the type being replaced" () {
    given:
      Iterable<ResolvedType> replaceables = [resolve(String)]
    when:
      replaceWildcardsFrom(replaceables, resolver.resolve(Map, WildcardType, WildcardType))
    then:
      thrown(IllegalStateException)
  }

  ResolvedType hashMap(def keyClazz, def valueClazz) {
    resolver.resolve(Map, keyClazz, valueClazz)
  }

  ResolvedType nestedGenericType(def clazz) {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, clazz))
  }

  ResolvedType resolve(Class clazz, Type ... typeBindings) {
    resolver.resolve(clazz, typeBindings)
  }
}
