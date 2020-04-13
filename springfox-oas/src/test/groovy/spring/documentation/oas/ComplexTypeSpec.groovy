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
import spock.lang.Unroll
import springfox.documentation.oas.mappers.SchemaMapper
import springfox.documentation.schema.Category
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.schema.ModelTestingSupport
import springfox.documentation.schema.ScalarType
import springfox.documentation.schema.mixins.ModelProviderSupport

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ComplexTypeSpec extends Specification implements ModelProviderSupport, ModelTestingSupport {
  @Shared
  def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()


  @Unroll
  def "Property #property on ComplexType is inferred correctly"() {
    given:
    def provider = defaultModelSpecificationProvider()
    ModelSpecification asInput = provider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(complexType()),
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    ModelSpecification asReturn = provider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            resolver.resolve(complexType()),
            Optional.empty(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

    then:
    request.getType() == "object"
    request.getName() == "ComplexType"
    request.getProperties().containsKey(property)
    request.properties[property] == type

    response.getType() == "object"
    response.getName() == "ComplexType"
    response.getProperties().containsKey(property)
    response.properties[property] == type

    where:
    property     | type
    "name"       | new StringSchema()
    "age"        | new IntegerSchema()
    "category"   | new ObjectSchema().$ref("Category")
    "customType" | new NumberSchema()
  }

  def "recursive type properties are inferred correctly"() {
    given:
    def complexType = resolver.resolve(recursiveType())
    def provider = defaultModelSpecificationProvider()
    ModelSpecification asInput = provider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            complexType,
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    ModelSpecification asReturn = provider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            complexType,
            Optional.empty(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

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
    "parent" | new ObjectSchema().$ref("RecursiveType")
  }

  @Unroll
  def "Inherited property #property is inferred correctly"() {
    given:
    def complexType = resolver.resolve(inheritedComplexType())
    def provider = defaultModelSpecificationProvider()
    ModelSpecification asInput = provider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            complexType,
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    ModelSpecification asReturn = provider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            complexType,
            Optional.empty(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

    then:
    request.getType() == "object"
    request.getName() == "InheritedComplexType"
    request.getProperties().containsKey(property)
    request.properties[property] == type

    response.getType() == "object"
    response.getName() == "InheritedComplexType"
    response.getProperties().containsKey(property)
    response.properties[property] == type

    where:
    property            | type
    "name"              | new StringSchema()
    "age"               | new IntegerSchema()
    "category"          | new ObjectSchema().$ref("Category")
    "customType"        | new NumberSchema()
    "inheritedProperty" | new StringSchema()
  }
}
