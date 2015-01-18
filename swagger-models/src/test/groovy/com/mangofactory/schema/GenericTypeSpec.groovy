package com.mangofactory.schema
import com.mangofactory.service.model.Model
import com.mangofactory.swagger.mixins.TypesForTestingSupport

import static com.google.common.base.Strings.*
import static com.mangofactory.schema.plugins.ModelContext.*

@Mixin([TypesForTestingSupport])
class GenericTypeSpec extends SchemaSpecification {
  def "Generic property on a generic types is inferred correctly for inbound types"() {
    given:

      def inputContext = inputParam(modelType, documentationType)
      Model asInput = modelProvider.modelFor(inputContext).get()

      def returnContext = returnValue(modelType, documentationType)
      Model asReturn = modelProvider.modelFor(returnContext).get()
    expect:
      asInput.getName() == expectedModelName(modelNamePart)
      asInput.getProperties().containsKey("genericField")
      def modelProperty = asInput.getProperties().get("genericField")
      def typeName = typeNameExtractor.typeName(fromParent(inputContext, modelProperty.getType()))
      typeName == propertyType
      modelProperty.getQualifiedType() == qualifiedType
      (modelProperty.getItems() == null) == (!propertyType.startsWith("List") && !propertyType.startsWith("Array"))

      asReturn.getName() == expectedModelName(modelNamePart)
      asReturn.getProperties().containsKey("genericField")
      def retModelProperty = asReturn.getProperties().get("genericField")
      typeNameExtractor.typeName(fromParent(returnContext, retModelProperty.getType())) == propertyType
      retModelProperty.getQualifiedType() == qualifiedType
      (retModelProperty.getItems() == null) == (!propertyType.startsWith("List") && !propertyType.startsWith("Array"))
    where:
      modelType                       | propertyType                                  | modelNamePart                                 | qualifiedType
      genericClass()                  | "SimpleType"                                  | "SimpleType"                                  | "com.mangofactory.schema.SimpleType"
      genericClassWithTypeErased()    | "object"                                      | ""                                            | "java.lang.Object"
      genericClassWithListField()     | "List"                                        | "List«SimpleType»"                            | "java.util.List<com.mangofactory.schema.SimpleType>"
      genericClassWithGenericField()  | "ResponseEntityAlternative«SimpleType»"       | "ResponseEntityAlternative«SimpleType»"       | "com.mangofactory.swagger.generics.ResponseEntityAlternative<com.mangofactory.schema.SimpleType>"
      genericClassWithDeepGenerics()  | "ResponseEntityAlternative«List«SimpleType»»" | "ResponseEntityAlternative«List«SimpleType»»" | "com.mangofactory.swagger.generics.ResponseEntityAlternative<java.util.List<com.mangofactory.schema.SimpleType>>"
      genericCollectionWithEnum()     | "Collection«string»"                          | "Collection«string»"                          | "java.util.Collection<com.mangofactory.schema.ExampleEnum>"
      genericTypeWithPrimitiveArray() | "Array"                                       | "Array«byte»"                                 | "byte"
      genericTypeWithComplexArray()   | "Array"                                       | "Array«SimpleType»"                           | null
  }

  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
      def inputContext = inputParam(modelType, documentationType)
      Model asInput = modelProvider.modelFor(inputContext).get()

      def returnContext = returnValue(modelType, documentationType)
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
      modelType                      | propertyType     | qualifiedType
      genericClass()                 | "List"           | "java.util.List<java.lang.String>"
      genericClassWithTypeErased()   | "List"           | "java.util.List<java.lang.String>"
      genericClassWithListField()    | "List"           | "java.util.List<java.lang.String>"
      genericClassWithGenericField() | "List"           | "java.util.List<java.lang.String>"
      genericClassWithDeepGenerics() | "List"           | "java.util.List<java.lang.String>"
      genericCollectionWithEnum()    | "List"           | "java.util.List<java.lang.String>"
  }

  def expectedModelName(String modelName) {
    if (!isNullOrEmpty(modelName)) {
      String.format("GenericType«%s»", modelName)
    } else {
      "GenericType"
    }
  }
}
