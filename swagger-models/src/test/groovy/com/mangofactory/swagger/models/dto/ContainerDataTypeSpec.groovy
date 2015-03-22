package com.mangofactory.swagger.models.dto

class ContainerDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize a primitive"() {
    expect:
      writePretty(new ContainerDataType("int", false)) == '''{
  "items" : {
    "format" : "int32",
    "type" : "integer"
  },
  "type" : "array",
  "uniqueItems" : false
}'''
  }

  def "should serialize a complex"() {
    expect:
      writePretty(new ContainerDataType("pet", false)) == '''{
  "items" : {
    "type" : "pet"
  },
  "type" : "array",
  "uniqueItems" : false
}'''
  }

  def "should serialize a complex with uniqueItems"() {
    expect:
      writePretty(new ContainerDataType("pet", true)) == '''{
  "items" : {
    "type" : "pet"
  },
  "type" : "array",
  "uniqueItems" : true
}'''
  }

  def "should fail to serialize a nested array"() {
    when:
      new ContainerDataType("array", false)
    then:
      thrown(IllegalArgumentException)
  }
}
