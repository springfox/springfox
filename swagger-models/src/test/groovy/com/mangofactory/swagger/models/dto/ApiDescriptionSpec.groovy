package com.mangofactory.swagger.models.dto

import spock.lang.Specification

class ApiDescriptionSpec extends Specification {

  def "should pass coverage"() {
    ApiDescription description = new ApiDescription('p', 'd', [], true)
    expect:
      description.getDescription()
      description.getOperations() == []
      description.getPath()
      description.isHidden()
  }
}
