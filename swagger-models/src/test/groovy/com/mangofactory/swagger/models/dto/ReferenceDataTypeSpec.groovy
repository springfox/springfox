package com.mangofactory.swagger.models.dto

class ReferenceDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize"() {
    expect:
      writePretty(new ReferenceDataType('Pet')) == '''{
  "$ref" : "Pet"
}'''
  }
}
