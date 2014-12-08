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
      //TODO - this does not look right - see https://github.com/swagger-api/swagger-spec/blob/master/versions/1.2.md#529-property-object
      writePretty(modelProperty) == """{
  "description" : "decs",
  "items" : {
    "qualifiedType" : "mqual",
    "ref" : "mref",
    "type" : "mtype"
  },
  "position" : 1,
  "qualifiedType" : "com.qual",
  "required" : true,
  "type" : "atype"
}"""
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
