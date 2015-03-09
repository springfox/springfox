package com.mangofactory.swagger.models.dto

class ModelPropertySpec extends InternalJsonSerializationSpec {
  final ModelProperty modelProperty = new ModelProperty(
          'atype',
          'com.qual',
          1,
          true,
          'decs',
          null,
          new ModelRef('mtype'), true
  )

  def "should serialize"() {
    expect:
      writePretty(modelProperty) == '''{
  "description" : "decs",
  "items" : {
    "type" : "mtype"
  },
  "required" : true,
  "type" : "atype",
  "uniqueItems" : true
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
        isUniqueItems()
      }
  }

}
