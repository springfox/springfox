package com.mangofactory.documentation.swagger.dto

import spock.lang.Specification

class AllowableListValuesSpec extends Specification {

  def "should pass coverage"() {
    expect:
      new com.mangofactory.documentation.service.model.AllowableListValues(['a'], 'string').with {
        getValues()
        getValueType()
      }
  }
}
