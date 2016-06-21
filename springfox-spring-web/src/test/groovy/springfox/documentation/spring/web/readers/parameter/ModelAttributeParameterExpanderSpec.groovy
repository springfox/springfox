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

package springfox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import springfox.documentation.service.Parameter
import springfox.documentation.spring.web.dummy.models.ModelAttributeExample
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.dummy.models.Example
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static springfox.documentation.schema.AlternateTypeRules.*

@Mixin([ServicePluginsSupport])
class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {
  List<Parameter> parameters = []
  TypeResolver typeResolver
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = new TypeResolver()
    plugin.alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
    sut = new ModelAttributeParameterExpander(typeResolver)
    sut.pluginsManager = defaultWebPlugins()
  }

  def "should expand parameters"() {
    when:
      sut.expand("", Example, parameters, context());
    then:
      parameters.size() == 9
      parameters.find { it.name == 'parentBeanProperty' }
      parameters.find { it.name == 'foo' }
      parameters.find { it.name == 'bar' }
      parameters.find { it.name == 'enumType' }
      parameters.find { it.name == 'annotatedEnumType' }
      parameters.find { it.name == 'allCapsSet' }
      parameters.find { it.name == 'nestedType.name' }
      parameters.find { it.name == 'localDateTime' }
  }

  def "should expand lists and nested types"() {
    when:
      sut.expand("", ModelAttributeExample, parameters, context());
    then:
      parameters.size() == 5
      parameters.find { it.name == 'stringProp' }
      parameters.find { it.name == 'intProp' }
      parameters.find { it.name == 'listProp' }
      parameters.find { it.name == 'arrayProp' }
      parameters.find { it.name == 'complexProp.name' }
  }
  
  def "should expand complex types"() {
	  when:
		sut.expand("", ModelAttributeComplexTypeExample, parameters, context());
	  then:
		parameters.size() == 11
		parameters.find { it.name == 'stringProp' }
		parameters.find { it.name == 'intProp' }
		parameters.find { it.name == 'listProp' }
		parameters.find { it.name == 'arrayProp' }
		parameters.find { it.name == 'complexProp.name' }
		parameters.find { it.name == 'fancyPets[#ind].categories[#ind].name' }
		parameters.find { it.name == 'fancyPets[#ind].id' }
		parameters.find { it.name == 'fancyPets[#ind].name' }
		parameters.find { it.name == 'fancyPets[#ind].age' }
		parameters.find { it.name == 'categories[#ind].name' }
		parameters.find { it.name == 'modelAttributeProperty' }	
  }

  def "should expand parameters when parent name is not empty"() {
    when:
      sut.expand("parent", Example, parameters, context());
    then:
      parameters.size() == 9
      parameters.find { it.name == 'parent.parentBeanProperty' }
      parameters.find { it.name == 'parent.foo' }
      parameters.find { it.name == 'parent.bar' }
      parameters.find { it.name == 'parent.enumType' }
      parameters.find { it.name == 'parent.annotatedEnumType' }
      parameters.find { it.name == 'parent.allCapsSet' }
      parameters.find { it.name == 'parent.nestedType.name' }
      parameters.find { it.name == 'parent.localDateTime' }
  }

  def "Should return empty set when there is an exception"() {
    given:
      ModelAttributeParameterExpander expander =
              new ModelAttributeParameterExpander(typeResolver) {
        @Override
        def BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
          throw new IntrospectionException("Fail");
        }
      }
      expander.pluginsManager = defaultWebPlugins()
    when:
      expander.expand("", Example, parameters, context());
    then:
      parameters.size() == 0;
  }
}
