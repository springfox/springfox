/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

import com.google.common.collect.ImmutableSet
import spock.lang.Unroll
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static com.google.common.base.Strings.*
import static springfox.documentation.schema.Collections.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class GenericTypeSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  @Unroll
  def "Generic property on a generic types is inferred correctly for types"() {
    given:
      def inputContext = inputParam(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      def returnContext = returnValue(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
        def propertyLookup = ["GenericType": "genericField", "Resource": "content"]
    when:
      List asInputContexts = modelProvider.modelsFor(inputContext)
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
    and:
      List asReturnContexts = modelProvider.modelsFor(returnContext)
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
    then:
      asInputModels.containsKey(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      def asInput = asInputModels.get(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      verifyModelProperty(asInput, propertyType, qualifiedType, propertyLookup[modelType.erasedType.simpleName])
    and:
      asReturnModels.containsKey(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      def asReturn = asReturnModels.get(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      verifyModelProperty(asInput, propertyType, qualifiedType, propertyLookup[modelType.erasedType.simpleName])

    
    where:
      modelType                       | propertyType                                  | modelNamePart                                 | qualifiedType
      genericClass()                  | "SimpleType"                                  | "SimpleType"                                  | "springfox.documentation.schema.SimpleType"
      genericClassWithTypeErased()    | "object"                                      | ""                                            | "java.lang.Object"
      genericClassWithListField()     | "List"                                        | "List«SimpleType»"                            | "java.util.List<springfox.documentation.schema.SimpleType>"
      genericClassWithGenericField()  | "ResponseEntityAlternative«SimpleType»"       | "ResponseEntityAlternative«SimpleType»"       | "springfox.documentation.schema.ResponseEntityAlternative<springfox.documentation.schema.SimpleType>"
      genericClassWithDeepGenerics()  | "ResponseEntityAlternative«List«SimpleType»»" | "ResponseEntityAlternative«List«SimpleType»»" | "springfox.documentation.schema.ResponseEntityAlternative<java.util.List<springfox.documentation.schema.SimpleType>>"
      genericCollectionWithEnum()     | "Collection«string»"                          | "Collection«string»"                          | "java.util.Collection<springfox.documentation.schema.ExampleEnum>"
      genericTypeWithPrimitiveArray() | "Array"                                       | "Array«byte»"                                 | "byte"
      genericTypeWithComplexArray()   | "Array"                                       | "Array«SimpleType»"                           | null
      genericResource()               | "SubclassOfResourceSupport"                   | "SubclassOfResourceSupport"                   | null
  }

  @Unroll
  def "Void generic type bindings are rendered correctly"() {
    given:
      def inputContext = inputParam(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      def returnContext = returnValue(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
    when:
      List asInputContexts = modelProvider.modelsFor(inputContext)
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
    and:
      List asReturnContexts = modelProvider.modelsFor(returnContext)
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    then:
      asInputModels.containsKey("GenericTypeBoundToMultiple«Void,Void»")
      def asInput = asInputModels.get("GenericTypeBoundToMultiple«Void,Void»")
      verifyModelProperty(asInput, propertyType, qualifiedType, propertyName)

    and:
      asReturnModels.containsKey("GenericTypeBoundToMultiple«Void,Void»")
      def asReturn = asReturnModels.get("GenericTypeBoundToMultiple«Void,Void»")
      verifyModelProperty(asReturn, propertyType, qualifiedType, propertyName)

    where:
      modelType            | propertyName | propertyType  | qualifiedType
      typeWithVoidLists()  | "a"          | "Void"        | "java.lang.Void"
      typeWithVoidLists()  | "listOfB"    | "List"        | "java.util.List<java.lang.Void>"
  }

  @Unroll
  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
      def inputContext = inputParam(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      List asInputContexts = modelProvider.modelsFor(inputContext)
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

      def returnContext = returnValue(
          modelType,
          documentationType,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())
      List asReturnContexts = modelProvider.modelsFor(returnContext)
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.containsKey(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      def asInput = asInputModels.get(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      asInput.getProperties().containsKey("strings")
      def modelProperty = asInput.getProperties().get("strings")
      typeNameExtractor.typeName(fromParent(inputContext, modelProperty.getType())) == propertyType
//      modelProperty.qualifiedType == qualifiedType

      asReturnModels.containsKey(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      def asReturn = asReturnModels.get(expectedModelName(modelNamePart, modelType.erasedType.simpleName))
      asReturn.getProperties().containsKey("strings")
      def retModelProperty = asReturn.getProperties().get("strings")
      typeNameExtractor.typeName(fromParent(inputContext, retModelProperty.getType())) == propertyType
//      retModelProperty.qualifiedType == qualifiedType // Not working as expected because of bug with classmate

    where:
      modelType                      | propertyType     | qualifiedType                      | modelNamePart
      genericClass()                 | "List"           | "java.util.List<java.lang.String>" | "SimpleType"
      genericClassWithTypeErased()   | "List"           | "java.util.List<java.lang.String>" | ""
      genericClassWithListField()    | "List"           | "java.util.List<java.lang.String>" | "List«SimpleType»"
      genericClassWithGenericField() | "List"           | "java.util.List<java.lang.String>" | "ResponseEntityAlternative«SimpleType»"
      genericClassWithDeepGenerics() | "List"           | "java.util.List<java.lang.String>" | "ResponseEntityAlternative«List«SimpleType»»"
      genericCollectionWithEnum()    | "List"           | "java.util.List<java.lang.String>" | "Collection«string»"
  }

  String expectedModelName(String modelName, String hostType = "GenericType") {
    if (!isNullOrEmpty(modelName)) {
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
      assert item.itemType == maybeTransformVoid(collectionElementType(modelProperty.type).erasedType.simpleName)
    }
  }

  def maybeTransformVoid(propertyType) {
    "void".equalsIgnoreCase(propertyType) ? propertyType.toLowerCase() : propertyType
  }
}
