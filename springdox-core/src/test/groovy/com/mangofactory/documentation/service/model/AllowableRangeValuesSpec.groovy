package com.mangofactory.documentation.service.model

import com.mangofactory.documentation.service.AllowableRangeValues
import spock.lang.Specification

class AllowableRangeValuesSpec extends Specification {
  def "Bean properties test" () {
    given:
      def sut = new AllowableRangeValues("0", "2")
    expect:
      sut.min == "0"
      sut.max == "2"
  }
}
