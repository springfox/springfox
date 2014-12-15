package com.mangofactory.swagger.models.dto

class PrimitiveFormatDataTypeSpec extends InternalJsonSerializationSpec {

  final PrimitiveFormatDataType primitiveFormatParameterType = new PrimitiveFormatDataType('type', 'format')

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
