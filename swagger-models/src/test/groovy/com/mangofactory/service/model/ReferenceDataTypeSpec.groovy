package com.mangofactory.service.model

class ReferenceDataTypeSpec extends InternalJsonSerializationSpec {

  def "should serialize"() {
    expect:
      writePretty(new ReferenceDataType('Pet')) == '''{
  "type" : "Pet"
}'''
  }
}
