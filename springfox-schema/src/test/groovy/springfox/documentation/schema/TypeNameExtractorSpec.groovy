/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import static java.util.Collections.*
import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class TypeNameExtractorSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  TypeResolver resolver = new TypeResolver()

  def "Response class for container types are inferred correctly"() {
    given:
    def context = returnValue("0_0",
        "group",
        containerType,
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    expect:
    typeNameExtractor.typeName(context) == name

    where:
    containerType                   | name
    genericListOfSimpleType()       | "List"
    genericListOfInteger()          | "List"
    erasedList()                    | "List"
    genericSetOfSimpleType()        | "Set"
    genericSetOfInteger()           | "Set"
    erasedSet()                     | "Set"
    genericClassWithGenericField()  | "GenericType«ResponseEntityAlternative«SimpleType»»"
    hashMap(String, SimpleType)     | "Map«string,SimpleType»"
    hashMap(String, String)         | "Map«string,string»"
    genericTypeWithPrimitiveArray() | "GenericType«Array«byte»»"
  }

  def "Input class for container types are inferred correctly"() {
    given:
    def context = inputParam("0_0",
        "group",
        containerType,
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    expect:
    typeNameExtractor.typeName(context) == name

    where:
    containerType                  | name
    genericListOfSimpleType()      | "List"
    genericListOfInteger()         | "List"
    erasedList()                   | "List"
    genericSetOfSimpleType()       | "Set"
    genericSetOfInteger()          | "Set"
    erasedSet()                    | "Set"
    genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"
    hashMap(String, SimpleType)    | "Map«string,SimpleType»"
    hashMap(String, String)        | "Map«string,string»"
  }

  def "Extractor for different contexts with same type produced different names"() {
    given:
    def contextInput = inputParam("0_0",
        "group",
        hashMap(SimpleType, genericListOfSimpleType()),
        Optional.empty(),
        new HashSet<>(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def contextReturn = returnValue("0_1",
        "group",
        genericClassWithGenericField(),
        Optional.empty(),
        SWAGGER_12,
        alternateTypeProvider(),
        namingStrategy,
        emptySet())
    def knownNames = new HashMap<String, String>()
    knownNames.put("0_0_springfox.documentation.schema.SimpleType", "SimpleType_1")
    knownNames.put("0_1_springfox.documentation.schema.SimpleType", "SimpleType_2")
    knownNames.put("0_1_springfox.documentation.schema.GenericType" +
        "<springfox.documentation.schema.ResponseEntityAlternative" +
        "<springfox.documentation.schema.SimpleType>>", "GenericType_4«ResponseEntityAlternative«SimpleType_2»»")
    expect:
    def nameInput = typeNameExtractor.typeName(contextInput)
    nameInput == "Map«SimpleType,List«SimpleType»»"

    def nameReturn = typeNameExtractor.typeName(contextReturn)
    nameReturn == "GenericType«ResponseEntityAlternative«SimpleType»»"
    and:
    def nameInputAdjusted = typeNameExtractor.typeName(contextInput, knownNames)
    nameInputAdjusted == "Map«SimpleType_1,List«SimpleType_1»»"

    def nameReturnAdjusted = typeNameExtractor.typeName(contextReturn, knownNames)
    nameReturnAdjusted == "GenericType_4«ResponseEntityAlternative«SimpleType_2»»"
  }

  //TODO: test cases for parent (withAndWithout)
}
