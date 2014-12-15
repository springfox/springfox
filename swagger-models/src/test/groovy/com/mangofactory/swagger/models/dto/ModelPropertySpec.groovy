package com.mangofactory.swagger.models.dto

class ModelPropertySpec extends InternalJsonSerializationSpec {
  final ModelProperty modelProperty = new ModelProperty(
          'atype',
          'com.qual',
          1,
          true,
          'decs',
          null,
          new ModelRef('mtype', 'mref', 'mqual')
  )

  def "should serialize"() {
    expect:
      writePretty(modelProperty) == '''{
  "description" : "decs",
  "items" : {
    "$ref" : "mref",
    "type" : "mtype"
  },
  "required" : true,
  "$ref" : "atype"
}'''
  }

  def "should pass coverage"() {
    expect:
      modelProperty.with {
        getType()
        getQualifiedType()
        getPosition()
        isRequired()
        getDescription()
        getAllowableValues()
        getItems()
      }
  }

}
