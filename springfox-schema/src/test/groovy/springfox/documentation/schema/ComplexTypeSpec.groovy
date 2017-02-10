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
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ComplexTypeSpec extends Specification {
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "complex type properties are inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      List asInputContexts = provider.modelsFor(inputParam(
          complexType(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = provider.modelsFor(returnValue(
          complexType(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputModels.size() == 2
      asInputModels.containsKey(modelName)
      def asInput = asInputModels.get(modelName)
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null
      
      asReturnContexts.size() == 2
      asReturnModels.containsKey(modelName)
      def asReturn = asReturnModels.get(modelName)
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == typeName
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null
      
    where:
      modelName     | property     | type         | typeName     | qualifiedType
      "ComplexType" | "name"       | String       | "string"     | "java.lang.String"
      "ComplexType" | "age"        | Integer.TYPE | "int"        | "int"
      "ComplexType" | "category"   | Category     | "Category"   | "springfox.documentation.schema.Category"
      "ComplexType" | "customType" | BigDecimal   | "bigdecimal" | "java.math.BigDecimal"
      "Category"    | "name"       | String       | "string"     | "java.lang.String"
  }

  def "recursive type properties are inferred correctly"() {
    given:
      def complexType = recursiveType()
      def provider = defaultModelProvider()
      List asInputContexts = provider.modelsFor(inputParam(
          complexType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      
      List asReturnContexts = provider.modelsFor(returnValue(
          complexType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))

    expect:
      asInputContexts.size() == 2
      asInputContexts.each {
        def asInput = it.builder.build()
        if (asInput.getName() == "RecursiveType") {
          asInput.getProperties().size() == 1
          asInput.getProperties().containsKey(property)
          def modelProperty = asInput.getProperties().get(property)
          modelProperty.type.erasedType == type
          modelProperty.getQualifiedType() == qualifiedType
          modelProperty.getModelRef().type == "RecursiveType"
          !modelProperty.getModelRef().collection
          modelProperty.getModelRef().itemType == null
        }
      }

      asReturnContexts.size() == 2
      asReturnContexts.each {
        def asReturn = it.builder.build()
        if (asReturn.getName() == "RecursiveType") {
          asReturn.getProperties().size() == 1
          asReturn.getProperties().containsKey(property)
          def retModelProperty = asReturn.getProperties().get(property)
          retModelProperty.type.erasedType == type
          retModelProperty.getQualifiedType() == qualifiedType
          retModelProperty.getModelRef().type == "RecursiveType"
          !retModelProperty.getModelRef().collection
          retModelProperty.getModelRef().itemType == null
        }
      }

    where:
      property | type          | qualifiedType
      "parent" | RecursiveType | "springfox.documentation.schema.RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      List asInputContexts = provider.modelsFor(inputParam(
          complexType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asInputModels = asInputContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};
      
      List asReturnContexts = provider.modelsFor(returnValue(
          complexType,
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build()))
      Map asReturnModels = asReturnContexts.collectEntries{
          [it.builder.build().getName(), it.builder.build()]};

    expect:
      asInputContexts.size() == 2
      asInputModels.containsKey(modelName)
      def asInput = asInputModels.get(modelName)   
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

      asReturnContexts.size() == 2
      asReturnModels.containsKey(modelName)
      def asReturn = asReturnModels.get(modelName)
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == typeName
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null
      
    where:
      modelName              |property             | type         | typeName     | typeProperty | qualifiedType
      "InheritedComplexType" | "name"              | String       | "string"     | 'type'       | "java.lang.String"
      "InheritedComplexType" | "age"               | Integer.TYPE | "int"        | 'type'       | "int"
      "InheritedComplexType" | "category"          | Category     | "Category"   | 'reference'  | "springfox.documentation.schema.Category"
      "InheritedComplexType" | "customType"        | BigDecimal   | "bigdecimal" | 'type'       | "java.math.BigDecimal"
      "InheritedComplexType" | "inheritedProperty" | String       | "string"     | 'type'       | "java.lang.String"
      "Category"             | "name"              | String       | "string"     | 'type'       | "java.lang.String"
  }
}
