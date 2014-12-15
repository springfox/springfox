package com.mangofactory.swagger.models.dto

class ApiDescriptionSpec extends InternalJsonSerializationSpec {
  final ApiDescription description = new ApiDescription('p', 'd', [], true)

  def "should serialize"() {
    expect:
      writePretty(description) == """{
  "path" : "p",
  "description" : "d",
  "operations" : [ ]
}"""
  }

  def "should pass coverage"() {
    expect:
      description.getDescription()
      description.getOperations() == []
      description.getPath()
      description.isHidden()
  }
}
