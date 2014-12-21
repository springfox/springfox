package com.mangofactory.swagger.models.servicemodel

class ReferenceDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize"() {
    expect:
      writePretty(new ReferenceDataType('Pet')) == '''{
  "type" : "Pet"
}'''
  }
}
