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
package springfox.documentation.schema.property.constructor

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.SchemaSpecification
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import springfox.documentation.schema.property.field.FieldModelProperty
import springfox.documentation.service.AllowableListValues

import static java.util.Collections.*
import static springfox.documentation.schema.property.BeanPropertyDefinitions.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ConstructorModelPropertySpec extends SchemaSpecification implements ModelPropertyLookupSupport {
  @Shared def resolver = new TypeResolver()

  def "Extracting information from resolved constructor params"() {
    given:
    def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
    def typeToTest = typeWithConstructorProperty()
    def beanPropertyDefinition = beanPropertyDefinitionByField(typeToTest, fieldName)
    def modelContext = inputParam("0_0",
        "group",
        resolver.resolve(typeToTest),
        Optional.empty(),
        new HashSet<>(),
        documentationType,
        alternateTypeProvider(),
        genericNamingStrategy,
        emptySet())
    def field = field(typeToTest, fieldName)
    ObjectMapper mapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy()
    namingStrategy.onApplicationEvent(new ObjectMapperConfigured(this, mapper))
    String propName = name(beanPropertyDefinition, true, namingStrategy, "")
    def sut = new FieldModelProperty(
        propName,
        field,
        resolver,
        alternateTypeProvider(),
        beanPropertyDefinition)

    expect:
    sut.propertyDescription() == description
    sut.required == isRequired
    typeNameExtractor.typeName(fromParent(modelContext, sut.getType())) == typeName
    sut.qualifiedTypeName() == qualifiedTypeName
    if (allowableValues != null) {
      sut.allowableValues() == new AllowableListValues(new ArrayList(allowableValues), "string")
    } else {
      sut.allowableValues() == null
    }
    sut.getName() == fieldName
    sut.getType() == field.getType()


    where:
    fieldName             || description | isRequired | typeName | qualifiedTypeName                                                    | allowableValues
    "foobar"              || null        | false      | "string" | "springfox.documentation.schema.TypeWithConstructorProperty\$Foobar" | ["Foo", "Bar"]
    "visibleForSerialize" || null        | false      | "long"   | "java.lang.Long"                                                     | null
  }
}
