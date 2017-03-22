/*
 *
 *  Copyright 2015-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License")
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

package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spring.web.dummy.models.Example
import springfox.documentation.spring.web.dummy.models.ModelAttributeComplexTypeExample
import springfox.documentation.spring.web.dummy.models.ModelAttributeExample
import springfox.documentation.spring.web.dummy.models.SomeType
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static springfox.documentation.schema.AlternateTypeRules.*

@Mixin([ServicePluginsSupport])
class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {
  TypeResolver typeResolver
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = new TypeResolver()
    plugin.alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
    sut = new ModelAttributeParameterExpander(new FieldProvider(typeResolver))
    sut.pluginsManager = defaultWebPlugins()
  }

  def "should expand parameters"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 10
    parameters.find { it.name == 'parentBeanProperty' }
    parameters.find { it.name == 'foo' }
    parameters.find { it.name == 'bar' }
    parameters.find { it.name == 'readOnlyString' }
    parameters.find { it.name == 'enumType' }
    parameters.find { it.name == 'annotatedEnumType' }
    parameters.find { it.name == 'propertyWithNoSetterMethod' }
    parameters.find { it.name == 'allCapsSet' }
    parameters.find { it.name == 'nestedType.name' }
    parameters.find { it.name == 'localDateTime' }
  }

  def "should expand lists and nested types"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(ModelAttributeExample), context()))

    then:
    parameters.size() == 6
    parameters.find { it.name == 'stringProp' }
    parameters.find { it.name == 'intProp' }
    parameters.find { it.name == 'listProp' }
    parameters.find { it.name == 'arrayProp' }
    parameters.find { it.name == 'complexProp.name' }
    parameters.find { it.name == 'accountTypes' }
  }

  def "should expand complex types"() {
    when:
    def parameters = sut.expand(new ExpansionContext("", typeResolver.resolve(ModelAttributeComplexTypeExample), context()))

    then:
    parameters.size() == 12
    parameters.find { it.name == 'stringProp' }
    parameters.find { it.name == 'intProp' }
    parameters.find { it.name == 'listProp' }
    parameters.find { it.name == 'arrayProp' }
    parameters.find { it.name == 'complexProp.name' }
    parameters.find { it.name == 'fancyPets[0].categories[0].name' }
    parameters.find { it.name == 'fancyPets[0].id' }
    parameters.find { it.name == 'fancyPets[0].name' }
    parameters.find { it.name == 'fancyPets[0].age' }
    parameters.find { it.name == 'categories[0].name' }
    parameters.find { it.name == 'modelAttributeProperty' }
    parameters.find { it.name == 'accountTypes' }
  }

  def "should expand parameters when parent name is not empty"() {
    when:
    def parameters = sut.expand(new ExpansionContext("parent", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 10
    parameters.find { it.name == 'parent.parentBeanProperty' }
    parameters.find { it.name == 'parent.foo' }
    parameters.find { it.name == 'parent.bar' }
    parameters.find { it.name == 'parent.enumType' }
    parameters.find { it.name == 'parent.annotatedEnumType' }
    parameters.find { it.name == 'parent.propertyWithNoSetterMethod' }
    parameters.find { it.name == 'parent.allCapsSet' }
    parameters.find { it.name == 'parent.nestedType.name' }
    parameters.find { it.name == 'parent.localDateTime' }
  }

  def "should not expand causing stack overflow"() {
    when:
    def parameters = sut.expand(new ExpansionContext("parent", typeResolver.resolve(SomeType), context()))

    then:
    parameters.size() == 3
    parameters.find { it.name == 'parent.string1' }
    parameters.find { it.name == 'parent.otherType.string2' }
    parameters.find { it.name == 'parent.otherType.parent.string1' }
  }

  def "Should return empty set when there is an exception"() {
    given:
    ModelAttributeParameterExpander expander =
        new ModelAttributeParameterExpander(new FieldProvider(typeResolver)) {
          @Override
          def BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
            throw new IntrospectionException("Fail")
          }
        }
    expander.pluginsManager = defaultWebPlugins()

    when:
    def parameters = expander.expand(new ExpansionContext("", typeResolver.resolve(Example), context()))

    then:
    parameters.size() == 0
  }
}
