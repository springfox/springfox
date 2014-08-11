package com.mangofactory.swagger.models.property

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import spock.lang.Specification

class BeanPropertyDefinitionsSpec extends Specification {
  def "Should return the internal bean name"() {
    given:
      BeanPropertyDefinition beanPropertyDefinition = Mock(BeanPropertyDefinition)

    when:
      def name = BeanPropertyDefinitions.beanPropertyByInternalName().apply(beanPropertyDefinition)
    then:
      1 * beanPropertyDefinition.getInternalName() >> "aName"
      name == "aName"
  }

}
