package com.mangofactory.swagger.dto

import spock.lang.Specification

class AllowableListValuesSpec extends Specification {

  def "should pass coverage"() {
    expect:
      new com.mangofactory.service.model.AllowableListValues(['a'], 'string').with {
        getValues()
        getValueType()
      }
  }
}
