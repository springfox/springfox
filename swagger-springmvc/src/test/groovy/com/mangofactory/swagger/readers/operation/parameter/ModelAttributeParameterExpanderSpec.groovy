package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.service.model.Parameter
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import org.joda.time.LocalDateTime
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.mangofactory.schema.alternates.Alternates.*

@Mixin(SpringSwaggerConfigSupport)
class ModelAttributeParameterExpanderSpec extends Specification {

  def "should expand an parameters"() {
    setup:
      List<Parameter> parameters = []
      def defaultValues = defaults(Mock(ServletContext))

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
}
