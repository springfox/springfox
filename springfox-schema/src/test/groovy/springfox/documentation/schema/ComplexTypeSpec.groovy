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

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.mixins.ModelProviderSupport

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ComplexTypeSpec extends Specification implements ModelProviderSupport {
  @Shared def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "complex type properties are inferred correctly"() {
    given:
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam("0_0",
        "group",
        resolver.resolve(complexType()),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    Model asReturn = provider.modelFor(returnValue("0_0",
        "group",
        resolver.resolve(complexType()),
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    expect:
    asInput.getName() == "ComplexType"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    modelProperty.getModelRef().type == typeName
    !modelProperty.getModelRef().collection
    modelProperty.getModelRef().itemType == null

    asReturn.getName() == "ComplexType"
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getQualifiedType() == qualifiedType
    retModelProperty.getModelRef().type == typeName
    !retModelProperty.getModelRef().collection
    retModelProperty.getModelRef().itemType == null

    where:
    property     | type         | typeName     | qualifiedType
    "name"       | String       | "string"     | "java.lang.String"
    "age"        | Integer.TYPE | "int"        | "int"
    "category"   | Category     | "Category"   | "springfox.documentation.schema.Category"
    "customType" | BigDecimal   | "bigdecimal" | "java.math.BigDecimal"
  }

  def "recursive type properties are inferred correctly"() {
    given:
    def complexType = resolver.resolve(recursiveType())
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam("0_0",
        "group",
        complexType,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()
    Model asReturn = provider.modelFor(returnValue("0_0",
        "group",
        complexType,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    expect:
    asInput.getName() == "RecursiveType"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    modelProperty.getModelRef().type == "RecursiveType"
    !modelProperty.getModelRef().collection
    modelProperty.getModelRef().itemType == null

    asReturn.getName() == "RecursiveType"
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getQualifiedType() == qualifiedType
    retModelProperty.getModelRef().type == "RecursiveType"
    !retModelProperty.getModelRef().collection
    retModelProperty.getModelRef().itemType == null

    where:
    property | type          | qualifiedType
    "parent" | RecursiveType | "springfox.documentation.schema.RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
    def complexType = resolver.resolve(inheritedComplexType())
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam("0_0",
        "group",
        complexType,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    Model asReturn = provider.modelFor(returnValue("0_0",
        "group",
        complexType,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())).get()

    expect:
    asInput.getName() == "InheritedComplexType"
    asInput.getProperties().containsKey(property)
    def modelProperty = asInput.getProperties().get(property)
    modelProperty.type.erasedType == type
    modelProperty.getQualifiedType() == qualifiedType
    modelProperty.getModelRef().type == typeName
    !modelProperty.getModelRef().collection
    modelProperty.getModelRef().itemType == null

    asReturn.getName() == "InheritedComplexType"
    asReturn.getProperties().containsKey(property)
    def retModelProperty = asReturn.getProperties().get(property)
    retModelProperty.type.erasedType == type
    retModelProperty.getQualifiedType() == qualifiedType
    retModelProperty.getModelRef().type == typeName
    !retModelProperty.getModelRef().collection
    retModelProperty.getModelRef().itemType == null

    where:
    property            | type         | typeName     | typeProperty | qualifiedType
    "name"              | String       | "string"     | 'type'       | "java.lang.String"
    "age"               | Integer.TYPE | "int"        | 'type'       | "int"
    "category"          | Category     | "Category"   | 'reference'  | "springfox.documentation.schema.Category"
    "customType"        | BigDecimal   | "bigdecimal" | 'type'       | "java.math.BigDecimal"
    "inheritedProperty" | String       | "string"     | 'type'       | "java.lang.String"
  }
}
