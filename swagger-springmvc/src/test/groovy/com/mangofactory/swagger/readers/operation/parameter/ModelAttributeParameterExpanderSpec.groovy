package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.dummy.models.Example
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.dto.Parameter
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static com.mangofactory.swagger.models.alternates.Alternates.newRule

@Mixin(SpringSwaggerConfigSupport)
class ModelAttributeParameterExpanderSpec extends Specification {

  def "should expand an parameters"() {
    setup:
      List<Parameter> parameters = []
      SpringSwaggerConfig config = springSwaggerConfig()

      AlternateTypeProvider alternateTypeProvider = config.defaultAlternateTypeProvider()
      TypeResolver typeResolver = config.typeResolver

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
