package com.mangofactory.swagger.models.dto

class PrimitiveFormatParameterTypeSpec extends InternalJsonSerializationSpec {

  final PrimitiveFormatParameterType primitiveFormatParameterType = new PrimitiveFormatParameterType('type', 'format')

  def "should serialize"() {
    expect:
      writePretty(primitiveFormatParameterType) == """{
  "format" : "format",
  "type" : "type"
}"""
  }

  def "should pass coverage"() {
    expect:
      primitiveFormatParameterType.format
      primitiveFormatParameterType.type
  }
}
