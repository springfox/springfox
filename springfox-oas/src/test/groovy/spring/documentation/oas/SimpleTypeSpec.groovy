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
import io.swagger.v3.oas.models.media.BooleanSchema
import io.swagger.v3.oas.models.media.ByteArraySchema
import io.swagger.v3.oas.models.media.DateSchema
import io.swagger.v3.oas.models.media.DateTimeSchema
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.NumberSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.media.UUIDSchema
import org.mapstruct.factory.Mappers
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.oas.mappers.SchemaMapper
import springfox.documentation.schema.CodeGenGenericTypeNamingStrategy
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.schema.ModelTestingSupport
import springfox.documentation.schema.SchemaSpecification

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class SimpleTypeSpec extends SchemaSpecification implements ModelTestingSupport {
  @Shared
  def resolver = new TypeResolver()
  @Shared
  def namingStrategy = new CodeGenGenericTypeNamingStrategy()

  @Unroll
  def "simple type is rendered as [#type]"() {
    given:
    ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
            Optional.empty(),
            new HashSet<>(),
            SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            resolver.resolve(simpleType()),
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
    request.getName() == "SimpleType"
    request.getProperties().containsKey(property)
    request.properties[property] == type

    response.getType() == "object"
    response.getName() == "SimpleType"
    response.getProperties().containsKey(property)
    response.properties[property] == type

    where:
    property          | type
    "aString"         | new StringSchema()
    "aByte"           | new ByteArraySchema()
    "aBoolean"        | new BooleanSchema()
    "aShort"          | new IntegerSchema()
    "anInt"           | new IntegerSchema()
    "aLong"           | new IntegerSchema()
    "aFloat"          | new NumberSchema()
    "aDouble"         | new NumberSchema()
    "anObjectByte"    | new ByteArraySchema()
    "anObjectBoolean" | new BooleanSchema()
    "anObjectShort"   | new IntegerSchema()
    "anObjectInt"     | new IntegerSchema()
    "anObjectLong"    | new IntegerSchema()
    "anObjectFloat"   | new NumberSchema()
    "anObjectDouble"  | new NumberSchema()
    "uuid"            | new UUIDSchema()
    "aDate"           | new DateSchema()
    "aSqlDate"        | new DateTimeSchema()
  }


  def "Types with properties aliased using JsonProperty annotation"() {
    given:
    ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(
        inputParam(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            new HashSet<>(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(
        returnValue(
            "0_0",
            "group",
            resolver.resolve(typeWithJsonPropertyAnnotation()),
            Optional.empty(),
            documentationType,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    when:
    def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
    def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

    then:
    request.getType() == "object"
    request.getName() == "TypeWithJsonProperty"
    request.getProperties().containsKey(property)
    request.properties[property] == type

    response.getType() == "object"
    response.getName() == "TypeWithJsonProperty"
    response.getProperties().containsKey(property)
    response.properties[property] == type

    where:
    property                    | type
    "some_custom_odd_ball_name" | new StringSchema()
  }

  Schema<?> stringSchema(String name) {
    new StringSchema()
        .name(name)
  }
}
