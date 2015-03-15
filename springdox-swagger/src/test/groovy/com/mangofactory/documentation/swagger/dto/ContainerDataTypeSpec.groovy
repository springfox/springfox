package com.mangofactory.documentation.swagger.dto

class ContainerDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize a primitive"() {
    expect:
      writePretty(new ContainerDataType("int", false)) == '''{
  "type" : "array",
  "items" : {
    "format" : "int32",
    "type" : "integer"
  }
}'''
  }

  def "should serialize a complex"() {
    expect:
      writePretty(new ContainerDataType("pet", false)) == '''{
  "type" : "array",
  "items" : {
    "type" : "pet"
  }
}'''
  }

  def "should serialize a complex with uniqueItems"() {
    expect:
      writePretty(new ContainerDataType("pet", true)) == '''{
  "type" : "array",
  "items" : {
    "type" : "pet"
  },
  "uniqueItems" : true
}'''
  }

  def "should fail to serialize a nested array"() {
    when:
      new ContainerDataType("array", false)
    then:
      thrown(IllegalArgumentException)
  }

  def "should fail to serialize a null"() {
    when:
      new ContainerDataType(null, false)
    then:
      thrown(NullPointerException)
  }
}
