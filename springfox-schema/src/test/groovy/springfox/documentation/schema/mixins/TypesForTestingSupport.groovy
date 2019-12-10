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

package springfox.documentation.schema.mixins

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.http.ResponseEntity
import org.springframework.ui.ModelMap
import springfox.documentation.schema.*

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
  static Class typeWithConstructorProperties() {
    TypeWithConstructorProperties
  }
  static Class typeWithDelegatedConstructor() {
    TypeWithDelegatedJsonCreatorConstructor
  }

  static Class typeWithJsonCreatorConstructor() {
    TypeWithJsonCreatorConstructor
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
  static Class collectionEnumType() {
    ExampleWithEnumCollection
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

  static ResolvedType nestedMapOfMaps() {
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

  static ResolvedType listOfMapOfStringToString() {
    resolver.resolve(List, resolver.resolve(Map, String, String))
  }

  static ResolvedType listOfMapOfStringToSimpleType() {
    resolver.resolve(List, resolver.resolve(Map, String, SimpleType))
  }

  static ResolvedType listOfErasedMap() {
    resolver.resolve(List, Map)
  }

  static ResolvedType listOfModelMap() {
    resolver.resolve(List, ModelMap)
  }

  static ResolvedType resources(def clazz) {
    resolver.resolve(CollectionModel, clazz)
  }

  static ResolvedType customMapOpen() {
    resolver.resolve(CustomMap)
  }

  static ResolvedType customMapOfType(def clazz) {
    resolver.resolve(CustomMap, clazz)
  }

  def ResolvedType typeForTestingPropertyPositions() {
    resolver.resolve(TypeForTestingPropertyPositions)
  }

  def ResolvedType typeWithVoidLists() {
    resolver.resolve(GenericTypeBoundToMultiple, Void.class, Void.class)
  }

  def ResolvedType genericEntityModel() {
    resolver.resolve(EntityModel, SubclassOfRepresentationModel.class)
  }

  static def nestedMaps() {
    resolver.resolve(Response, LanguageResponse)
  }
}
