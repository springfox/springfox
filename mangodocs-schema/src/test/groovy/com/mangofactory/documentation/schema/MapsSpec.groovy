package com.mangofactory.documentation.schema
import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification

class MapsSpec extends Specification {
  @Shared def resolver = new TypeResolver()
  def "detects that a given type is a Map" () {
    given:
      Maps.isMapType(resolver.resolve(type))
    where:
      type                                      | isMap
      String                                    | false
      SimpleType                                | false
      resolver.resolve(List, String)            | false
      resolver.resolve(Map, String, SimpleType) | true
      resolver.resolve(Map, String, Object)     | true
      resolver.resolve(Map, Object, Object)     | true
      resolver.resolve(Map)                     | true
  }

  def "provides value type of the map" () {
    given:
      Maps.mapValueType(resolver.resolve(type)).getErasedSignature().equals(name)
    where:
      type                                      | name
      String                                    | "Object"
      SimpleType                                | "Object"
      resolver.resolve(List, String)            | "Object"
      resolver.resolve(Map, String, SimpleType) | "SimpleType"
      resolver.resolve(Map, String, Object)     | "Object"
      resolver.resolve(Map, Object, Object)     | "Object"
      resolver.resolve(Map, Object, SimpleType) | "SimpleType"
      resolver.resolve(Map)                     | "Object"
  }
}
