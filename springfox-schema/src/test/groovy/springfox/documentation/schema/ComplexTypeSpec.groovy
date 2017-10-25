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

import com.google.common.base.Optional
import com.google.common.collect.ImmutableSet
import spock.lang.Ignore
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
      Map<String, Model> models = new HashMap<String, Model>();

      Model asInput = provider.modelFor(inputParam("group",
          complexType(),
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = provider.modelFor(returnValue("group",
          complexType(),
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      model.getProperties().containsKey(property)
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

    where:
      modelName       | property     | type         | typeName     | qualifiedType
      "ComplexType"   | "name"       | String       | "string"     | "java.lang.String"
      "ComplexType"   | "age"        | Integer.TYPE | "int"        | "int"
      "ComplexType"   | "category"   | Category     | "Category"   | "springfox.documentation.schema.Category"
      "ComplexType"   | "customType" | BigDecimal   | "bigdecimal" | "java.math.BigDecimal"
      "ComplexType_1" | "name"       | String       | "string"     | "java.lang.String"
      "ComplexType_1" | "age"        | Integer.TYPE | "int"        | "int"
      "ComplexType_1" | "category"   | Category     | "Category_1" | "springfox.documentation.schema.Category"
      "ComplexType_1" | "customType" | BigDecimal   | "bigdecimal" | "java.math.BigDecimal"
  }

  def "recursive type properties are inferred correctly"() {
    given:
      def complexType = recursiveType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam("group",
          complexType,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()

      Model asReturn = provider.modelFor(returnValue("group",
          complexType,
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()

    expect:
      asInput.getName() == "RecursiveType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == "RecursiveType"
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

      asReturn.getName() == "RecursiveType_1"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == "RecursiveType_1"
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null

    where:
      property | type          | qualifiedType
      "parent" | RecursiveType | "springfox.documentation.schema.RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      Map<String, Model> models = new HashMap<String, Model>();

      Model asInput = provider.modelFor(inputParam("group",
          complexType,
          Optional.absent(),
          new HashSet<>(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asInput.getName(), asInput);

      Model asReturn = provider.modelFor(returnValue("group",
          complexType,
          Optional.absent(),
          SWAGGER_12,
          alternateTypeProvider(),
          namingStrategy,
          ImmutableSet.builder().build())).get()
      models.put(asReturn.getName(), asReturn);

    expect:
      models.containsKey(modelName);
      def model = models.get(modelName);
      def modelProperty = model.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

    where:
      modelName                | property            | type         | typeName     | typeProperty | qualifiedType
      "InheritedComplexType"   | "name"              | String       | "string"     | 'type'       | "java.lang.String"
      "InheritedComplexType"   | "age"               | Integer.TYPE | "int"        | 'type'       | "int"
      "InheritedComplexType"   | "category"          | Category     | "Category"   | 'reference'  | "springfox.documentation.schema.Category"
      "InheritedComplexType"   | "customType"        | BigDecimal   | "bigdecimal" | 'type'       | "java.math.BigDecimal"
      "InheritedComplexType"   | "inheritedProperty" | String       | "string"     | 'type'       | "java.lang.String"
      "InheritedComplexType_1" | "name"              | String       | "string"     | 'type'       | "java.lang.String"
      "InheritedComplexType_1" | "age"               | Integer.TYPE | "int"        | 'type'       | "int"
      "InheritedComplexType_1" | "category"          | Category     | "Category_1" | 'reference'  | "springfox.documentation.schema.Category"
      "InheritedComplexType_1" | "customType"        | BigDecimal   | "bigdecimal" | 'type'       | "java.math.BigDecimal"
      "InheritedComplexType_1" | "inheritedProperty" | String       | "string"     | 'type'       | "java.lang.String"
  }
}
