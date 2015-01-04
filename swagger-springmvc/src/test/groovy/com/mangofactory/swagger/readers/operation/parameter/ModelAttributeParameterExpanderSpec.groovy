package com.mangofactory.swagger.readers.operation.parameter
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.service.model.Parameter
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.models.Example
import org.joda.time.LocalDateTime

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static com.mangofactory.schema.alternates.Alternates.*

class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {

  def "should expand an parameters"() {
    setup:
      List<Parameter> parameters = []

      AlternateTypeProvider alternateTypeProvider = defaultValues.alternateTypeProvider
      TypeResolver typeResolver = defaultValues.typeResolver
      alternateTypeProvider.addRule(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))

      ModelAttributeParameterExpander expander = new ModelAttributeParameterExpander(alternateTypeProvider)
    when:
      expander.expand("", Example, parameters);
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
    setup:
      List<Parameter> parameters = []

      AlternateTypeProvider alternateTypeProvider = defaultValues.alternateTypeProvider
      TypeResolver typeResolver = defaultValues.typeResolver
      alternateTypeProvider.addRule(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))

      ModelAttributeParameterExpander expander = new ModelAttributeParameterExpander(alternateTypeProvider)
    when:
      expander.expand("parent", Example, parameters);
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
    setup:
      List<Parameter> parameters = []

      AlternateTypeProvider alternateTypeProvider = defaultValues.alternateTypeProvider
      TypeResolver typeResolver = defaultValues.typeResolver
      alternateTypeProvider.addRule(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))

      ModelAttributeParameterExpander expander = new ModelAttributeParameterExpander(alternateTypeProvider) {
        @Override
        def BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
          throw new IntrospectionException("Fail");
        }
      }
    when:
      expander.expand("", Example, parameters);
    then:
      parameters.size() == 0;
  }
}
