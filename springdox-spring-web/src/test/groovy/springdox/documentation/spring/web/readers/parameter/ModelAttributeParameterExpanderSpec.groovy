package springdox.documentation.spring.web.readers.parameter

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDateTime
import springdox.documentation.service.Parameter
import springdox.documentation.spring.web.dummy.models.Example
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

import java.beans.BeanInfo
import java.beans.IntrospectionException

import static springdox.documentation.schema.AlternateTypeRules.*

@Mixin([ServicePluginsSupport])
class ModelAttributeParameterExpanderSpec extends DocumentationContextSpec {
  List<Parameter> parameters = []
  TypeResolver typeResolver
  ModelAttributeParameterExpander sut

  def setup() {
    typeResolver = new TypeResolver()
    plugin.alternateTypeRules(newRule(typeResolver.resolve(LocalDateTime), typeResolver.resolve(String)))
    sut = new ModelAttributeParameterExpander(typeResolver, defaultWebPlugins())
  }

  def "should expand parameters"() {
    when:
      sut.expand("", Example, parameters, context());
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

  def "should expand parameters when parent name is not empty"() {
    when:
      sut.expand("parent", Example, parameters, context());
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
              new ModelAttributeParameterExpander(typeResolver, defaultWebPlugins()) {
        @Override
        def BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
          throw new IntrospectionException("Fail");
        }
      }
    when:
      expander.expand("", Example, parameters, context());
    then:
      parameters.size() == 0;
  }
}
