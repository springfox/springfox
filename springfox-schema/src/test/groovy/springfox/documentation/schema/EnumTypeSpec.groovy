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
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.spi.DocumentationType

import java.util.stream.Stream

import static java.util.Collections.*
import static java.util.stream.Collectors.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class EnumTypeSpec extends Specification implements ModelProviderSupport {
  @Shared def resolver = new TypeResolver()
  
  def "enum type are inferred as type string with allowable values"() {
    given:
    def list = Stream.of("ONE", "TWO").collect(toList())
    def provider = defaultModelProvider()
    def namingStrategy = new DefaultGenericTypeNamingStrategy()

    Model asInput = provider.modelFor(
        inputParam("0_0",
            "group",
            resolver.resolve(enumType()),
            Optional.empty(),
            new HashSet<>(),
            DocumentationType.SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()
    Model asReturn = provider.modelFor(
        returnValue("0_0",
            "group",
            resolver.resolve(enumType()),
            Optional.empty(),
            DocumentationType.SWAGGER_12,
            alternateTypeProvider(),
            namingStrategy,
            emptySet())).get()

    expect:
    asInput.getName() == "ExampleWithEnums"
    asInput.getProperties().containsKey("exampleEnum")
    def modelPropertyOption = asInput.getProperties().get("exampleEnum")
    def modelProperty = modelPropertyOption


    modelProperty.type.erasedType == ExampleEnum
    modelProperty.getQualifiedType() == "springfox.documentation.schema.ExampleEnum"
    modelProperty.getModelRef().type == "string"
    !modelProperty.getModelRef().collection
    modelProperty.getModelRef().itemType == null
    modelProperty.getAllowableValues().getValues() == list

    asReturn.getName() == "ExampleWithEnums"
    asReturn.getProperties().containsKey("exampleEnum")
    def retModelPropertyOption = asReturn.getProperties().get("exampleEnum")
    def retModelProperty = retModelPropertyOption
    retModelProperty.type.erasedType == ExampleEnum
    retModelProperty.getQualifiedType() == "springfox.documentation.schema.ExampleEnum"
    retModelProperty.getModelRef().type == "string"
    !retModelProperty.getModelRef().collection
    retModelProperty.getModelRef().itemType == null
    retModelProperty.getAllowableValues().getValues() == list
  }
}
