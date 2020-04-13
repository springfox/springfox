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
import io.swagger.v3.oas.models.media.*
import org.mapstruct.factory.Mappers
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.oas.mappers.SchemaMapper
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.schema.ModelTestingSupport
import springfox.documentation.schema.SchemaSpecification

import static java.util.Collections.emptySet
import static springfox.documentation.spi.DocumentationType.OAS_30
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam
import static springfox.documentation.spi.schema.contexts.ModelContext.returnValue

class ContainerTypesSpec extends SchemaSpecification implements ModelTestingSupport {
    @Shared
    def resolver = new TypeResolver()
    @Shared
    def namingStrategy = new DefaultGenericTypeNamingStrategy()

    @Unroll
    def "Model property #property of type List, is inferred correctly"() {
        given:
        def sut = resolver.resolve(typeWithLists())
        ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(
                inputParam("0_0",
                        "group",
                        sut,
                        Optional.empty(),
                        new HashSet<>(),
                        OAS_30,
                        alternateTypeProvider(),
                        namingStrategy,
                        emptySet())).get()
        ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(
                returnValue("0_0",
                        "group",
                        sut,
                        Optional.empty(),
                        OAS_30,
                        alternateTypeProvider(),
                        namingStrategy,
                        emptySet())).get()

        when:
        def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
        def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

        then:
        request.getType() == "object"
        request.getName() == "ListsContainer"
        request.getProperties().containsKey(property)
        request.properties[property] == type

        response.getType() == "object"
        response.getName() == "ListsContainer"
        response.getProperties().containsKey(property)
        response.properties[property] == type

        where:
        property          | type
        "complexTypes"    | new ArraySchema().items(new ObjectSchema().$ref("ComplexType"))
        "enums"           | new ArraySchema().items(new StringSchema()) //TODO: enum
        "aliasOfIntegers" | new ArraySchema().items(new IntegerSchema())
        "strings"         | new ArraySchema().items(new StringSchema())
        "objects"         | new ArraySchema().items(new ObjectSchema())
        "substituted"     | new ArraySchema().items(new ObjectSchema().$ref("Substituted"))
    }

    @Unroll
    def "Model property #property of type SET, is inferred correctly"() {
        given:
        def sut = resolver.resolve(typeWithSets())
        ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(inputParam("0_0",
                "group",
                sut,
                Optional.empty(),
                new HashSet<>(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()
        ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(returnValue("0_0",
                "group",
                sut,
                Optional.empty(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()

        when:
        def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
        def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

        then:
        request.getType() == "object"
        request.getName() == "SetsContainer"
        request.getProperties().containsKey(property)
        request.properties[property] == type

        response.getType() == "object"
        response.getName() == "SetsContainer"
        response.getProperties().containsKey(property)
        response.properties[property] == type

        where:
        property          | type
        "complexTypes"    | new ArraySchema().items(new ObjectSchema().$ref("ComplexType")).uniqueItems(true)
        "enums"           | new ArraySchema().items(new StringSchema()).uniqueItems(true) //TODO: enum
        "aliasOfIntegers" | new ArraySchema().items(new IntegerSchema()).uniqueItems(true)
        "strings"         | new ArraySchema().items(new StringSchema()).uniqueItems(true)
        "objects"         | new ArraySchema().items(new ObjectSchema()).uniqueItems(true)
    }

    @Unroll
    def "Model properties of type Arrays are inferred correctly for #property"() {
        given:
        def sut = resolver.resolve(typeWithArrays())
        ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(inputParam("0_0",
                "group",
                sut,
                Optional.empty(),
                new HashSet<>(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()
        ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(returnValue("0_0",
                "group",
                sut,
                Optional.empty(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()

        when:
        def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
        def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

        then:
        request.getType() == "object"
        request.getName() == "ArraysContainer"
        request.getProperties().containsKey(property)
        request.properties[property] == type

        response.getType() == "object"
        response.getName() == "ArraysContainer"
        response.getProperties().containsKey(property)
        response.properties[property] == type

        where:
        property               | type
        "complexTypes"         | new ArraySchema().items(new ObjectSchema().$ref("ComplexType"))
        "enums"                | new ArraySchema().items(new StringSchema()) //TODO: enum
        "aliasOfIntegers"      | new ArraySchema().items(new IntegerSchema())
        "strings"              | new ArraySchema().items(new StringSchema())
        "objects"              | new ArraySchema().items(new ObjectSchema())
        "bytes"                | new ByteArraySchema()
        "substituted"          | new ArraySchema().items(new ObjectSchema().$ref("Substituted"))
        "arrayOfArrayOfInts"   | new ArraySchema().items(new ArraySchema().items(new IntegerSchema()))
        "arrayOfListOfStrings" | new ArraySchema().items(new ArraySchema().items(new StringSchema()))
    }

    @Unroll
    def "Map Model properties #property is inferred correctly for OpenAPI 3.0"() {
        given:
        def sut = resolver.resolve(mapsContainer())

        ModelSpecification asInput = modelSpecificationProvider.modelSpecificationsFor(inputParam("0_0",
                "group",
                sut,
                Optional.empty(),
                new HashSet<>(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()
        ModelSpecification asReturn = modelSpecificationProvider.modelSpecificationsFor(returnValue("0_0",
                "group",
                sut,
                Optional.empty(),
                OAS_30,
                alternateTypeProvider(),
                namingStrategy,
                emptySet())).get()

        when:
        def request = Mappers.getMapper(SchemaMapper).mapFrom(asInput)
        def response = Mappers.getMapper(SchemaMapper).mapFrom(asReturn)

        then:
        request.getType() == "object"
        request.getName() == "MapsContainer"
        request.getProperties().containsKey(property)
        request.properties[property] == type

        response.getType() == "object"
        response.getName() == "MapsContainer"
        response.getProperties().containsKey(property)
        response.properties[property] == type

        where:
        property                       | type
        "enumToSimpleType"             | new MapSchema().additionalProperties(new ObjectSchema().$ref("SimpleType"))
        "stringToSimpleType"           | new MapSchema().additionalProperties(new ObjectSchema().$ref("SimpleType"))
        "complexToSimpleType"          | new MapSchema().additionalProperties(new ObjectSchema().$ref("SimpleType"))
        "complexToString"              | new MapSchema().additionalProperties(new StringSchema())
        "mapOfmapOfStringToSimpleType" | new MapSchema().additionalProperties(new ObjectSchema().$ref("Map«string,SimpleType»"))
    }
}
                            