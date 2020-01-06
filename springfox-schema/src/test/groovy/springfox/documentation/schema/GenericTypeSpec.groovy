/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static java.util.Collections.*
import static org.springframework.util.StringUtils.*
import static springfox.documentation.schema.Collections.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class GenericTypeSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  @Unroll
  def "Generic property on a generic types is inferred correctly for types"() {
    given:
    def inputContext = inputParam(
        "0_0",
        "group",
        modelType,
        Optional.empty(),
        new HashSet<>(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())

    def returnContext = returnValue("0_0",
        "group",
        modelType,
        Optional.empty(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def propertyLookup = ["GenericType": "genericField", "EntityModel": "links"]

    when:
    Model asInput = modelProvider.modelFor(inputContext).get()

    and:
    Model asReturn = modelProvider.modelFor(returnContext).get()

    then:
    asInput.getName() == expectedModelName(modelNamePart, modelType.erasedType.simpleName)
    verifyModelProperty(asInput, propertyType, qualifiedType, propertyLookup[modelType.erasedType.simpleName])

    and:
    asReturn.getName() == expectedModelName(modelNamePart, modelType.erasedType.simpleName)
    verifyModelProperty(asInput, propertyType, qualifiedType, propertyLookup[modelType.erasedType.simpleName])

    where:
    modelType                       | propertyType                                  | modelNamePart                                 | qualifiedType
    genericClass()                  | "SimpleType"                                  | "SimpleType"                                  | "springfox.documentation.schema.SimpleType"
    genericClassWithTypeErased()    | "object"                                      | ""                                            | "java.lang.Object"
    genericClassWithListField()     | "List"                                        | "List«SimpleType»"                            | "java.util.List<springfox.documentation.schema.SimpleType>"
    genericClassWithGenericField()  | "ResponseEntityAlternative«SimpleType»"       | "ResponseEntityAlternative«SimpleType»"       | "springfox.documentation.schema.ResponseEntityAlternative<springfox.documentation.schema.SimpleType>"
    genericClassWithDeepGenerics()  | "ResponseEntityAlternative«List«SimpleType»»" | "ResponseEntityAlternative«List«SimpleType»»" | "springfox.documentation.schema.ResponseEntityAlternative<java.util.List<springfox.documentation.schema.SimpleType>>"
    genericCollectionWithEnum()     | "List"                                        | "List«string»"                                | "java.util.Collection<springfox.documentation.schema.ExampleEnum>"
    genericTypeWithPrimitiveArray() | "Array"                                       | "Array«byte»"                                 | "byte"
    genericTypeWithComplexArray()   | "Array"                                       | "Array«SimpleType»"                           | null
    genericEntityModel()            | "List"                                        | "SubclassOfRepresentationModel"               | null
  }

  @Unroll
  def "Void generic type bindings are rendered correctly"() {
    given:
    def inputContext = inputParam(
        "0_0",
        "group",
        modelType,
        Optional.empty(),
        new HashSet<>(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())

    def returnContext = returnValue("0_0",
        "group",
        modelType,
        Optional.empty(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())

    when:
    Model asInput = modelProvider.modelFor(inputContext).get()
    Model asReturn = modelProvider.modelFor(returnContext).get()

    then:
    asInput.getName() == "GenericTypeBoundToMultiple«Void,Void»"
    verifyModelProperty(asInput, propertyType, qualifiedType, propertyName)

    and:
    asReturn.getName() == "GenericTypeBoundToMultiple«Void,Void»"
    verifyModelProperty(asReturn, propertyType, qualifiedType, propertyName)

    where:
    modelType           | propertyName | propertyType | qualifiedType
    typeWithVoidLists() | "a"          | "Void"       | "java.lang.Void"
    typeWithVoidLists() | "listOfB"    | "List"       | "java.util.List<java.lang.Void>"
  }

  @Unroll
  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
    def inputContext = inputParam("0_0",
        "group",
        modelType,
        Optional.empty(),
        new HashSet<>(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    Model asInput = modelProvider.modelFor(inputContext).get()

    def returnContext = returnValue("0_0",
        "group",
        modelType,
        Optional.empty(),
        documentationType,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    Model asReturn = modelProvider.modelFor(returnContext).get()

    expect:
    asInput.getProperties().containsKey("strings")
    def modelProperty = asInput.getProperties().get("strings")
    typeNameExtractor.typeName(fromParent(inputContext, modelProperty.getType())) == propertyType
//      modelProperty.qualifiedType == qualifiedType

    asReturn.getProperties().containsKey("strings")
    def retModelProperty = asReturn.getProperties().get("strings")
    typeNameExtractor.typeName(fromParent(inputContext, retModelProperty.getType())) == propertyType
//      retModelProperty.qualifiedType == qualifiedType // Not working as expected because of bug with classmate

    where:
    modelType                      | propertyType | qualifiedType
    genericClass()                 | "List"       | "java.util.List<java.lang.String>"
    genericClassWithTypeErased()   | "List"       | "java.util.List<java.lang.String>"
    genericClassWithListField()    | "List"       | "java.util.List<java.lang.String>"
    genericClassWithGenericField() | "List"       | "java.util.List<java.lang.String>"
    genericClassWithDeepGenerics() | "List"       | "java.util.List<java.lang.String>"
    genericCollectionWithEnum()    | "List"       | "java.util.List<java.lang.String>"
  }

  def expectedModelName(String modelName, String hostType = "GenericType") {
    if (!isEmpty(modelName)) {
      "$hostType«$modelName»"
    } else {
      hostType
    }
  }

  void verifyModelProperty(Model model, String propertyType, qualifiedPropertyType, propertyName) {
    assert model.getProperties().containsKey(propertyName)
    ModelProperty modelProperty = model.properties.get(propertyName)
    modelProperty.qualifiedType == qualifiedPropertyType
    def item = modelProperty.modelRef
    assert item.type == maybeTransformVoid(propertyType)
    if (!propertyType.startsWith("List") && !propertyType.startsWith("Array")) {
      assert !item.collection
      assert item.itemType == null
    } else {
      assert item.collection
      def elementType = collectionElementType(modelProperty.type)
      if (elementType.erasedType.isEnum()) {
        assert item.itemType == maybeTransformVoid("string")
      } else {
        assert item.itemType == maybeTransformVoid(elementType.erasedType.simpleName)
      }
    }
  }

  def maybeTransformVoid(propertyType) {
    "void".equalsIgnoreCase(propertyType) ? propertyType.toLowerCase() : propertyType
  }
}
