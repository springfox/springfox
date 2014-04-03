package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.models.alternates.Entry
import org.springframework.http.ResponseEntity

class TypesForTestingSupport {
  static Class simpleType() {
    SimpleType
  }
  static Class complexType() {
    ComplexType
  }
  static Class enumType() {
    ExampleWithEnums
  }
  static Class typeWithLists() {
    ListsContainer
  }
  static Class typeWithSets() {
    SetsContainer
  }
  static Class typeWithArrays() {
    ArraysContainer
  }
  static Class recursiveType() {
    RecursiveType
  }
  static Class inheritedComplexType() {
    InheritedComplexType
  }

  static ResolvedType genericClassWithTypeErased() {
    def resolver = new TypeResolver()
    resolver.resolve(GenericType)
  }

  static ResolvedType genericClass() {
    new TypeResolver().resolve(GenericType, SimpleType)
  }

  static ResolvedType genericClassOfType(def type) {
    new TypeResolver().resolve(GenericType, type)
  }

  static ResolvedType genericClassWithListField() {
    def resolver = new TypeResolver()
    resolver.resolve(GenericType, resolver.resolve(List, SimpleType))
  }
  static ResolvedType genericClassWithGenericField() {
    def resolver = new TypeResolver()
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, SimpleType))
  }

  static ResolvedType genericClassWithDeepGenerics() {
    def resolver = new TypeResolver()
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, resolver.resolve(List, SimpleType)))
  }
  static ResolvedType genericCollectionWithEnum() {
    def resolver = new TypeResolver()
    resolver.resolve(GenericType, resolver.resolve(Collection, ExampleEnum))
  }

  static ResolvedType genericListOfSimpleType() {
    def resolver = new TypeResolver()
    resolver.resolve(List, SimpleType)
  }
  static ResolvedType genericListOfInteger() {
    def resolver = new TypeResolver()
    resolver.resolve(List, Integer)
  }
  static ResolvedType genericSetOfSimpleType() {
    def resolver = new TypeResolver()
    resolver.resolve(Set, SimpleType)
  }
  static ResolvedType genericSetOfInteger() {
    def resolver = new TypeResolver()
    resolver.resolve(Set, Integer)
  }

  static ResolvedType erasedList() {
    def resolver = new TypeResolver()
    resolver.resolve(List)
  }
  static ResolvedType erasedSet() {
    def resolver = new TypeResolver()
    resolver.resolve(Set)
  }
  static Class typeForTestingGettersAndSetters() {
    TypeWithGettersAndSetters
  }

  static ResolvedType hashMap(def keyClazz, def valueClazz) {
    def resolver = new TypeResolver()
    resolver.resolve(Map, keyClazz, valueClazz)
  }

  static ResolvedType genericMap(def toResolve, def key, def value) {
    def resolver = new TypeResolver()
    resolver.resolve(toResolve, resolver.resolve(Entry, key, value));
  }
}
