package com.mangofactory.swagger.models.property

import spock.lang.Specification

import static com.mangofactory.swagger.models.property.ApiModelProperties.allowableValueFromString

class ApiModelPropertiesSpec extends Specification {
  def "creates allowable list with multiple values" () {
    given:
      def list = allowableValueFromString(value)
    expect:
      list.values.size() == count
    where:
      value         | count
      ""            | 0
      " "           | 0
      "a"           | 1
      "a,b"         | 2
  }

  def "creates allowable range with multiple values" () {
    given:
      def range = allowableValueFromString(value)
    expect:
      range.min == min
      range.max == max
    where:
      value         | min   | max
      "range[1,2]"  | "1"   | "2"
  }
}
