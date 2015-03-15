package springdox.documentation.swagger.dto

import spock.lang.Specification

class AllowableListValuesSpec extends Specification {

  def "should pass coverage"() {
    expect:
      new springdox.documentation.service.AllowableListValues(['a'], 'string').with {
        getValues()
        getValueType()
      }
  }
}
