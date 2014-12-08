package com.mangofactory.swagger.models.dto

class ModelSpec extends InternalJsonSerializationSpec {

  final Model model = new Model("id", "name", 'qtype', ['propK': 'propV'], 'desc', 'bModel', 'discrim', ['subtype1'])

  def "should serialize"() {
    expect:
      writePretty(model) == """{
  "baseModel" : "bModel",
  "description" : "desc",
  "discriminator" : "discrim",
  "id" : "id",
  "name" : "name",
  "properties" : {
    "propK" : "propV"
  },
  "qualifiedType" : "qtype",
  "subTypes" : [ "subtype1" ]
}"""
  }

  def "should pass coverage"() {
    expect:
      model.baseModel
      model.description
      model.discriminator
      model.id
      model.name
      model.properties
      model.qualifiedType
      model.subTypes
  }
}
