package com.mangofactory.spring.web.readers.operation.parameter
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.service.model.Parameter
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.mixins.PluginsSupport
import org.joda.time.LocalDateTime

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static com.mangofactory.schema.alternates.Alternates.*

@Mixin([PluginsSupport])
class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {
  List<Parameter> parameters = []
  AlternateTypeProvider alternateTypeProvider
  TypeResolver typeResolver
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = defaultValues.typeResolver
    alternateTypeProvider = defaultValues.alternateTypeProvider
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
    sut = new ModelAttributeParameterExpander(alternateTypeProvider, typeResolver, springPluginsManager())
  }

  def "should expand an parameters"() {
    when:
      sut.expand("", Example, parameters, new DocumentationType("swagger", "1.2"));
    then:
      parameters.size() == 8
      parameters.find { it.name == 'parentBeanProperty' }
      parameters.find { it.name == 'foo' }
      parameters.find { it.name == 'bar' }
      parameters.find { it.name == 'enumType' }
      parameters.find { it.name == 'annotatedEnumType' }
      parameters.find { it.name == 'allCapsSet' }
      parameters.find { it.name == 'nestedType.name' }
      parameters.find { it.name == 'localDateTime' }
  }

  def "should expand an parameters when parent name is not empty"() {
    when:
      sut.expand("parent", Example, parameters, new DocumentationType("swagger", "1.2"));
    then:
      parameters.size() == 8
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
              new ModelAttributeParameterExpander(alternateTypeProvider, typeResolver, springPluginsManager()) {
        @Override
        def BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
          throw new IntrospectionException("Fail");
        }
      }
    when:
      expander.expand("", Example, parameters, new DocumentationType("swagger", "1.2"));
    then:
      parameters.size() == 0;
  }
}
