package com.mangofactory.servicemodel

import com.mangofactory.servicemodel.builder.ApiDescriptionBuilder

class ApiDescriptionSpec extends InternalJsonSerializationSpec {
  final ApiDescription description = new ApiDescriptionBuilder()
          .path('p')
          .description('d')
          .operations([])
          .hidden(true)
          .build()

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
