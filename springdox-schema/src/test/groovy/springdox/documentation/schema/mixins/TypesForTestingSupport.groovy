package springdox.documentation.schema.mixins

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import springdox.documentation.schema.*
import org.springframework.http.ResponseEntity
import springdox.documentation.schema.ArraysContainer
import springdox.documentation.schema.ComplexType
import springdox.documentation.schema.Entry
import springdox.documentation.schema.ExampleEnum
import springdox.documentation.schema.ExampleWithEnums
import springdox.documentation.schema.GenericType
import springdox.documentation.schema.InheritedComplexType
import springdox.documentation.schema.ListsContainer
import springdox.documentation.schema.MapsContainer
import springdox.documentation.schema.RecursiveType
import springdox.documentation.schema.ResponseEntityAlternative
import springdox.documentation.schema.SetsContainer
import springdox.documentation.schema.SimpleType
import springdox.documentation.schema.TypeForTestingPropertyNames
import springdox.documentation.schema.TypeWithAlternateProperty
import springdox.documentation.schema.TypeWithConstructor
import springdox.documentation.schema.TypeWithConstructorProperty
import springdox.documentation.schema.TypeWithJsonGetterAnnotation
import springdox.documentation.schema.TypeWithJsonProperty
import springdox.documentation.schema.UnwrappedType

class TypesForTestingSupport {

  public static final resolver = new TypeResolver()

  static Class simpleType() {
    SimpleType
  }
  static Class unwrappedType() {
    UnwrappedType
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
    resolver.resolve(GenericType, resolver.resolve(ResponseEntityAlternative, SimpleType))
  }

  static ResolvedType genericClassWithDeepGenerics() {
    resolver.resolve(GenericType, resolver.resolve(ResponseEntityAlternative, resolver.resolve(List, SimpleType)))
  }

  static ResolvedType genericTypeOfMapsContainer() {
    resolver.resolve(GenericType, mapsContainer())
  }

  static ResolvedType genericCollectionWithEnum() {
    resolver.resolve(GenericType, resolver.resolve(Collection, ExampleEnum))
  }

  static ResolvedType genericTypeWithPrimitiveArray() {
    resolver.resolve(GenericType, resolver.arrayType(byte.class))
  }

  static ResolvedType genericTypeWithComplexArray() {
    resolver.resolve(GenericType, resolver.arrayType(SimpleType.class))
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
  static Class typeForTestingPropertyNames() {
    TypeForTestingPropertyNames
  }

  static Class typeForTestingJsonGetterAnnotation(){
    TypeWithJsonGetterAnnotation
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
