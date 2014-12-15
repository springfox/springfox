package com.mangofactory.swagger.models.dto

class TypeOnlyDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize"() {
    expect:
      writePretty(new TypeOnlyDataType(new PrimitiveFormatDataType('integer', 'int32'))) == '''{
  "type" : "integer"
}'''
  }
}
