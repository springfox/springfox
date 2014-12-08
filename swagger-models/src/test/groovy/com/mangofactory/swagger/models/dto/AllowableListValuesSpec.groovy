package com.mangofactory.swagger.models.dto

import spock.lang.Specification

class AllowableListValuesSpec extends Specification {

  def "should pass coverage"() {
    expect:
      new AllowableListValues(['a'], 'string').with {
        getValues()
        getValueType()
      }
  }
}
