package com.mangofactory.swagger.dto

class ModelPropertySpec extends InternalJsonSerializationSpec {
  final ModelPropertyDto modelProperty = new ModelPropertyDto("aName",'atype'
          , 'com.qual'
          , 1
          , true
          , 'decs'
          , null
          , new DataType('mtype'))

  def "should serialize"() {
    expect:
      writePretty(modelProperty) == '''{
  "description" : "decs",
  "items" : {
    "type" : "mtype"
  },
  "required" : true,
  "type" : "atype"
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
