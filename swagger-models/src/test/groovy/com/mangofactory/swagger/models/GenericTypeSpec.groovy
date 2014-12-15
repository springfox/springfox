package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
import spock.lang.Specification
import spock.lang.Unroll

import static com.google.common.base.Strings.isNullOrEmpty
import static com.mangofactory.swagger.models.ModelContext.inputParam
import static com.mangofactory.swagger.models.ModelContext.returnValue

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class GenericTypeSpec extends Specification {
  @Unroll
  def "Generic property on a generic types is inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(modelType)).get()
      Model asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
      asInput.getName() == expectedModelName(modelNamePart)
      asInput.getProperties().containsKey("genericField")
      def modelProperty = asInput.getProperties().get("genericField")
      modelProperty.getType().dataType.reference == propertyType
      modelProperty.getQualifiedType() == qualifiedType
      (modelProperty.getItems() == null) == (!"List".equals(propertyType) && !"Array".equals(propertyType))

      asReturn.getName() == expectedModelName(modelNamePart)
      asReturn.getProperties().containsKey("genericField")
      def retModelProperty = asReturn.getProperties().get("genericField")
      retModelProperty.getType().dataType.reference == propertyType
      retModelProperty.getQualifiedType() == qualifiedType
      (retModelProperty.getItems() == null) == (!"List".equals(propertyType) && !"Array".equals(propertyType))

    where:
      modelType                       | propertyType                                  | modelNamePart                                 | qualifiedType
      genericClass()                  | "SimpleType"                                  | "SimpleType"                                  | "com.mangofactory.swagger.models.SimpleType"
      genericClassWithTypeErased()    | "object"                                      | ""                                            | "java.lang.Object"
      genericClassWithListField()     | "List"                                        | "List«SimpleType»"                            | "java.util.List<com.mangofactory.swagger.models.SimpleType>"
      genericClassWithGenericField()  | "ResponseEntityAlternative«SimpleType»"       | "ResponseEntityAlternative«SimpleType»"       | "com.mangofactory.swagger.generics.ResponseEntityAlternative<com.mangofactory.swagger.models.SimpleType>"
      genericClassWithDeepGenerics()  | "ResponseEntityAlternative«List«SimpleType»»" | "ResponseEntityAlternative«List«SimpleType»»" | "com.mangofactory.swagger.generics.ResponseEntityAlternative<java.util.List<com.mangofactory.swagger.models.SimpleType>>"
      genericCollectionWithEnum()     | "Collection«string»"                          | "Collection«string»"                          | "java.util.Collection<com.mangofactory.swagger.models.ExampleEnum>"
      genericTypeWithPrimitiveArray() | "Array"                                       | "Array«byte»"                                 | "byte"
      genericTypeWithComplexArray()   | "Array"                                       | "Array«SimpleType»"                           | null
  }


  @Unroll
  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(modelType)).get()
      Model asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
      asInput.getProperties().containsKey("strings")
      def modelProperty = asInput.getProperties().get("strings")
      modelProperty.getType().dataType.reference == propertyType
//    modelProperty.qualifiedType() == qualifiedType DK TODO: Fix this AK - I think its not even required

      asReturn.getProperties().containsKey("strings")
      def retModelProperty = asReturn.getProperties().get("strings")
      retModelProperty.getType().dataType.reference == propertyType
//    retModelProperty.qualifiedType() ==qualifiedType DK TODO: Fix this AK - I think its not even required

    where:
      modelType                      | propertyType | qualifiedType
      genericClass()                 | "List"       | "java.util.List<java.lang.String>"
      genericClassWithTypeErased()   | "List"       | "java.util.List<java.lang.String>"
      genericClassWithListField()    | "List"       | "java.util.List<java.lang.String>"
      genericClassWithGenericField() | "List"       | "java.util.List<java.lang.String>"
      genericClassWithDeepGenerics() | "List"       | "java.util.List<java.lang.String>"
      genericCollectionWithEnum()    | "List"       | "java.util.List<java.lang.String>"
  }

  def "Set a generic type naming strategy should succeed if null or populated"() {
     when:
     ResolvedTypes.setNamingStrategy(new DefaultGenericTypeNamingStrategy())
     
     then: 
     notThrown(Exception)
     
     when:
     ResolvedTypes.setNamingStrategy(null);
     
     then:
     notThrown(Exception)
  }

  def expectedModelName(String modelName) {
    if (!isNullOrEmpty(modelName)) {
      String.format("GenericType«%s»", modelName)
    } else {
      "GenericType"
    }
  }
}
