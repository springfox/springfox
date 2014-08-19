package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.models.alternates.Entry
import org.springframework.http.ResponseEntity

class TypesForTestingSupport {

  public static final resolver = new TypeResolver()

  static Class simpleType() {
    SimpleType
  }
  static Class typeWithConstructor() {
    TypeWithConstructor
  }
  static Class typeWithConstructorProperty() {
    TypeWithConstructorProperty
  }
  static Class mapsContainer() {
    MapsContainer
  }
  static Class typeWithJsonPropertyAnnotation() {
    TypeWithJsonProperty
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
    resolver.resolve(GenericType)
  }

  static ResolvedType genericClass() {
    resolver.resolve(GenericType, SimpleType)
  }

  static ResolvedType genericClassOfType(def type) {
    resolver.resolve(GenericType, type)
  }

  static ResolvedType genericClassWithListField() {
    resolver.resolve(GenericType, resolver.resolve(List, SimpleType))
  }
  static ResolvedType genericClassWithGenericField() {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, SimpleType))
  }

  static ResolvedType genericClassWithDeepGenerics() {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, resolver.resolve(List, SimpleType)))
  }

  static ResolvedType responseEntityWithDeepGenerics() {
    resolver.resolve(ResponseEntity, mapsContainer())
  }

  static ResolvedType genericCollectionWithEnum() {
    resolver.resolve(GenericType, resolver.resolve(Collection, ExampleEnum))
  }

  static ResolvedType genericListOfSimpleType() {
    resolver.resolve(List, SimpleType)
  }
  static ResolvedType genericListOfInteger() {
    resolver.resolve(List, Integer)
  }
  static ResolvedType genericSetOfSimpleType() {
    resolver.resolve(Set, SimpleType)
  }
  static ResolvedType genericSetOfInteger() {
    resolver.resolve(Set, Integer)
  }

  static ResolvedType erasedList() {
    resolver.resolve(List)
  }
  static ResolvedType erasedSet() {
    resolver.resolve(Set)
  }
  static Class typeForTestingGettersAndSetters() {
    TypeWithGettersAndSetters
  }

  static Class typeForTestingAnnotatedGettersAndSetter(){
    TypeWithAnnotatedGettersAndSetters
  }

  static ResolvedType hashMap(def keyClazz, def valueClazz) {
    resolver.resolve(Map, keyClazz, valueClazz)
  }

  static ResolvedType genericMap(def toResolve, def key, def value) {
    resolver.resolve(toResolve, resolver.resolve(Entry, key, value));
  }

  static ResolvedType typeWithAlternateProperty() {
    resolver.resolve(TypeWithAlternateProperty);
  }

  static ResolvedType typeWithResponseEntityOfVoid() {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, Void))
  }

  static ResolvedType nestedGenericType(def clazz) {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntity, clazz))
  }
}
