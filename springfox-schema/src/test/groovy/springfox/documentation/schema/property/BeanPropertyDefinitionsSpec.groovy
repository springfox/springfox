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

package springfox.documentation.schema.property

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import spock.lang.Specification
import spock.lang.Unroll

import static springfox.documentation.schema.property.BeanPropertyDefinitions.*

class BeanPropertyDefinitionsSpec extends Specification {
  def "Cannot instantiate static type"() {
    when:
    new BeanPropertyDefinitions()

    then:
    thrown(UnsupportedOperationException)
  }

  def "Should return the internal bean name"() {
    given:
    BeanPropertyDefinition beanPropertyDefinition = Mock(BeanPropertyDefinition)

    when:
    def name = beanPropertyByInternalName().apply(beanPropertyDefinition)

    then:
    1 * beanPropertyDefinition.getInternalName() >> "aName"
    name == "aName"
  }

  @Unroll
  def "Should apply unwrapping prefix correctly for #propertyName with #prefix"() {
    given:
    def strategy = new ObjectMapperBeanPropertyNamingStrategy()
    strategy.objectMapper = new ObjectMapper()
    def beanDefinition = Mock(BeanPropertyDefinition)

    when:
    beanDefinition.name >> "property"
    def name = name(
        beanDefinition,
        true,
        strategy,
        prefix)

    then:
    name == expectedName

    where:
    prefix | propertyName | expectedName
    "__"   | "property"   | "__property"
    ""     | "property"   | "property"

  }

}
