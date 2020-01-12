/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import springfox.documentation.schema.AlternateTypeRule
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.WildcardType
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spring.web.dummy.models.ModelAttributeWithHiddenParametersExample
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.DefaultConfiguration
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.parameter.ExpansionContext
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport

import static springfox.documentation.schema.AlternateTypeRules.*

class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec implements SwaggerPluginsSupport {
  TypeResolver typeResolver
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = new TypeResolver()
    plugin.alternateTypeRules(
        newRule(
            typeResolver.resolve(LocalDateTime),
            typeResolver.resolve(String)))

    sut = new ModelAttributeParameterExpander(
        new FieldProvider(typeResolver),
        new AccessorsProvider(typeResolver),
        new JacksonEnumTypeDeterminer())

    sut.pluginsManager = swaggerServicePlugins([
        new SwaggerDefaults(
            new Defaults(),
            new TypeResolver()
        )])
  }

  def "shouldn't expand hidden parameters"() {
    when:
    def parameters = sut.expand(
        new ExpansionContext(
            "",
            typeResolver.resolve(ModelAttributeWithHiddenParametersExample),
            context()))

    then:
    parameters.size() == 7
    parameters.find { it.name == 'modelAttributeProperty' }
    parameters.find { it.name == 'stringProp' }
    parameters.find { it.name == 'intProp' }
    parameters.find { it.name == 'listProp' }
    parameters.find { it.name == 'arrayProp' }
    parameters.find { it.name == 'complexProp.name' }
    parameters.find { it.name == 'accountTypes' }
  }

  class SwaggerDefaults implements DefaultsProviderPlugin {
    private final DefaultConfiguration defaultConfiguration
    private TypeResolver typeResolver

    @Autowired
    SwaggerDefaults(Defaults defaults, TypeResolver typeResolver) {
      this.typeResolver = typeResolver
      defaultConfiguration = new DefaultConfiguration(
          defaults,
          typeResolver,
          new DefaultPathProvider())
    }

    @Override
    DocumentationContextBuilder create(DocumentationType documentationType) {
      List<AlternateTypeRule> rules = new ArrayList<>()
      rules.add(newRule(typeResolver.resolve(Map.class, String.class, String.class),
          typeResolver.resolve(Object.class)))
      rules.add(newMapRule(WildcardType.class, WildcardType.class))
      return defaultConfiguration
          .create(documentationType)
          .rules(rules)
    }

    @Override
    boolean supports(DocumentationType delimiter) {
      return true
    }
  }
}
