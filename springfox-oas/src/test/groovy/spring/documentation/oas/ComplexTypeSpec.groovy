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

package spring.documentation.oas

import com.fasterxml.classmate.TypeResolver
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.NumberSchema
import io.swagger.v3.oas.models.media.ObjectSchema
import io.swagger.v3.oas.models.media.StringSchema
import org.mapstruct.factory.Mappers
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.oas.mappers.SchemaMapper
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelTestingSupport
import springfox.documentation.schema.mixins.ModelProviderSupport

class ComplexTypeSpec extends Specification implements ModelProviderSupport, ModelTestingSupport, ModelRegistrySupport {
  @Shared
  def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "Property #property on ComplexType is inferred correctly"() {
    given:
    def provider = defaultModelSpecificationProvider()
    def type = complexType()
    def (asInput, asReturn, modelNamesRegistry) =
    requestResponseAndNamesRegistry(provider, type)

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(
        asInput,
        modelNamesRegistry)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(
        asReturn,
        modelNamesRegistry)

    then:
    request.getType() == "object"
    request.getName() == "ComplexType"
    request.getProperties().containsKey(property)
    request.properties[property] == requestType

    response.getType() == "object"
    response.getName() == "ComplexType"
    response.getProperties().containsKey(property)
    response.properties[property] == responseType

    where:
    property     | requestType                                    | responseType
    "name"       | new StringSchema()                             | new StringSchema()
    "age"        | new IntegerSchema()                            | new IntegerSchema()
    "category"   | new ObjectSchema().type(null).$ref("Category") | new ObjectSchema().type(null).$ref("Category")
    "customType" | new NumberSchema().format("bigdecimal")        | new NumberSchema().format("bigdecimal")
  }

  def "recursive type properties are inferred correctly"() {
    given:
    def complexType = resolver.resolve(recursiveType())
    def provider = defaultModelSpecificationProvider()
    def (asInput, asReturn, modelNamesRegistry) =
    requestResponseAndNamesRegistry(provider, complexType)

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(
        asInput,
        modelNamesRegistry)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(
        asReturn,
        modelNamesRegistry)

    then:
    request.getType() == "object"
    request.getName() == "RecursiveType"
    request.getProperties().containsKey(property)
    request.properties[property] == type

    response.getType() == "object"
    response.getName() == "RecursiveType"
    response.getProperties().containsKey(property)
    response.properties[property] == type

    where:
    property | type
    "parent" | new ObjectSchema().type(null).$ref("RecursiveType")
  }

  def "Inherited property #property is inferred correctly"() {
    given:
    def complexType = resolver.resolve(inheritedComplexType())
    def provider = defaultModelSpecificationProvider()
    def (asInput, asReturn, modelNamesRegistry) =
    requestResponseAndNamesRegistry(provider, complexType)

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(
        asInput,
        modelNamesRegistry)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(
        asReturn,
        modelNamesRegistry)

    then:
    request.getType() == "object"
    request.getName() == "InheritedComplexType"
    request.getProperties().containsKey(property)
    request.properties[property] == requestType

    response.getType() == "object"
    response.getName() == "InheritedComplexType"
    response.getProperties().containsKey(property)
    response.properties[property] == responseType

    where:
    property            | requestType                                   | responseType
    "name"              | new StringSchema()                            | new StringSchema()
    "age"               | new IntegerSchema()                           | new IntegerSchema()
    "category"          | new ObjectSchema().type(null).$ref("Category")| new ObjectSchema().type(null).$ref("Category")
    "customType"        | new NumberSchema().format("bigdecimal")       | new NumberSchema().format("bigdecimal")
    "inheritedProperty" | new StringSchema()                            | new StringSchema()
  }
}
