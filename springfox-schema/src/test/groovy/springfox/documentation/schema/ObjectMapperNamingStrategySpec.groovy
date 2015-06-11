/*
 *
 *  Copyright 2015 the original author or authors.
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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import spock.lang.Specification
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.schema.mixins.ModelPropertyLookupSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy

@Mixin([ModelPropertyLookupSupport, TypesForTestingSupport])
class ObjectMapperNamingStrategySpec extends Specification {

  def "rename without setting an strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy()
      sut.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "anObject"
      "setaByte"             | "aByte"
      "getAnObjectBoolean"   | "anObjectBoolean"
      "setDate"              | "date"
  }

  def "rename setting snake_case strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy()
      sut.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))
      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "an_object"
      "setaByte"             | "a_byte"
      "getAnObjectBoolean"   | "an_object_boolean"
      "setDate"              | "date"
  }

  def "rename setting CamelCase strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy()
      sut.onApplicationEvent(new ObjectMapperConfigured(this, objectMapper))

      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "AnObject"
      "setaByte"             | "AByte"
      "getAnObjectBoolean"   | "AnObjectBoolean"
      "setDate"              | "Date"
  }
}
