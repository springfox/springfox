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

trait TypesForTestingSupport {

  Class simpleType() {
    SimpleType
  }
  Class unwrappedType() {
    UnwrappedType
  }
  Class typeWithConstructor() {
    TypeWithConstructor
  }
  Class typeWithConstructorProperty() {
    TypeWithConstructorProperty
  }
  Class typeWithConstructorProperties() {
    TypeWithConstructorProperties
  }
  Class typeWithDelegatedConstructor() {
    TypeWithDelegatedJsonCreatorConstructor
  }

  Class typeWithJsonCreatorConstructor() {
    TypeWithJsonCreatorConstructor
  }
  Class mapsContainer() {
    MapsContainer
  }
  Class typeWithJsonPropertyAnnotation() {
    TypeWithJsonProperty
  }
  Class complexType() {
    ComplexType
  }
  Class enumType() {
    ExampleWithEnums
  }
  Class collectionEnumType() {
    ExampleWithEnumCollection
  }
  Class typeWithLists() {
    ListsContainer
  }
  Class typeWithSets() {
    SetsContainer
  }
  Class typeWithArrays() {
    ArraysContainer
  }
  Class recursiveType() {
    RecursiveType
  }
  Class inheritedComplexType() {
    InheritedComplexType
  }

  ResolvedType genericClassWithTypeErased() {
    typeResolver().resolve(GenericType)
  }

  ResolvedType genericClass() {
    typeResolver().resolve(GenericType, SimpleType)
  }

  ResolvedType genericClassOfType(def type) {
    typeResolver().resolve(GenericType, type)
  }

  ResolvedType genericClassWithListField() {
    typeResolver().resolve(GenericType, typeResolver().resolve(List, SimpleType))
  }
  ResolvedType genericClassWithGenericField() {
    typeResolver().resolve(GenericType, typeResolver().resolve(ResponseEntityAlternative, SimpleType))
  }

  ResolvedType nestedMapOfMaps() {
    typeResolver().resolve(GenericType, typeResolver().resolve(ResponseEntityAlternative, SimpleType))
  }

  ResolvedType genericClassWithDeepGenerics() {
    typeResolver().resolve(GenericType, typeResolver().resolve(ResponseEntityAlternative, typeResolver().resolve(List, SimpleType)))
  }

  ResolvedType genericTypeOfMapsContainer() {
    typeResolver().resolve(GenericType, mapsContainer())
  }

  ResolvedType genericCollectionWithEnum() {
    typeResolver().resolve(GenericType, typeResolver().resolve(Collection, ExampleEnum))
  }

  ResolvedType genericTypeWithPrimitiveArray() {
    typeResolver().resolve(GenericType, typeResolver().arrayType(byte.class))
  }

  ResolvedType genericTypeWithComplexArray() {
    typeResolver().resolve(GenericType, typeResolver().arrayType(SimpleType.class))
  }

  ResolvedType genericListOfSimpleType() {
    typeResolver().resolve(List, SimpleType)
  }
  ResolvedType genericListOfInteger() {
    typeResolver().resolve(List, Integer)
  }
  ResolvedType genericSetOfSimpleType() {
    typeResolver().resolve(Set, SimpleType)
  }
  ResolvedType genericSetOfInteger() {
    typeResolver().resolve(Set, Integer)
  }

  ResolvedType erasedList() {
    typeResolver().resolve(List)
  }
  ResolvedType erasedSet() {
    typeResolver().resolve(Set)
  }
  Class typeForTestingPropertyNames() {
    TypeForTestingPropertyNames
  }

  Class typeForTestingJsonGetterAnnotation(){
    TypeWithJsonGetterAnnotation
  }

  ResolvedType hashMap(def keyClazz, def valueClazz) {
    typeResolver().resolve(Map, keyClazz, valueClazz)
  }

  ResolvedType genericMap(def toResolve, def key, def value) {
    typeResolver().resolve(toResolve, typeResolver().resolve(Entry, key, value));
  }

  ResolvedType typeWithAlternateProperty() {
    typeResolver().resolve(TypeWithAlternateProperty);
  }

  ResolvedType typeWithResponseEntityOfVoid() {
    typeResolver().resolve(GenericType, typeResolver().resolve(ResponseEntity, Void))
  }

  ResolvedType nestedGenericType(def clazz) {
    typeResolver().resolve(GenericType, typeResolver().resolve(ResponseEntity, clazz))
  }

  ResolvedType listOfMapOfStringToString() {
    typeResolver().resolve(List, typeResolver().resolve(Map, String, String))
  }

  ResolvedType listOfMapOfStringToSimpleType() {
    typeResolver().resolve(List, typeResolver().resolve(Map, String, SimpleType))
  }

  ResolvedType listOfErasedMap() {
    typeResolver().resolve(List, Map)
  }

  ResolvedType listOfModelMap() {
    typeResolver().resolve(List, ModelMap)
  }

  ResolvedType resources(def clazz) {
    typeResolver().resolve(CollectionModel, clazz)
  }

  ResolvedType customMapOpen() {
    typeResolver().resolve(CustomMap)
  }

  ResolvedType customMapOfType(def clazz) {
    typeResolver().resolve(CustomMap, clazz)
  }

  ResolvedType typeForTestingPropertyPositions() {
    typeResolver().resolve(TypeForTestingPropertyPositions)
  }

  ResolvedType typeWithVoidLists() {
    typeResolver().resolve(GenericTypeBoundToMultiple, Void.class, Void.class)
  }

  ResolvedType genericEntityModel() {
    typeResolver().resolve(EntityModel, SubclassOfRepresentationModel.class)
  }

  def nestedMaps() {
    typeResolver().resolve(Response, LanguageResponse)
  }

  def typeResolver() {
    new TypeResolver()
  }
}
