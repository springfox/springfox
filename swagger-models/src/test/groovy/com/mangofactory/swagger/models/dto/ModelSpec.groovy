package com.mangofactory.swagger.models.dto

import spock.lang.Unroll

class ModelSpec extends InternalJsonSerializationSpec {

  final Model model = new Model("id", "name", 'qtype', ['propK': 'propV'], 'desc', 'bModel', 'discrim', ['subtype1'])

  def "should serialize"() {
    expect:
      writePretty(model) == """{
  "baseModel" : "bModel",
  "description" : "desc",
  "discriminator" : "discrim",
  "id" : "id",
  "properties" : {
    "propK" : "propV"
  },
  "subTypes" : [ "subtype1" ]
}"""
  }

  @Unroll
  def "should serialize ignoring optional fields"() {
    final Model model = new Model("id", "name", 'qtype', ['propK': 'propV'], 'desc', val, val, listVal)

    expect:
      writePretty(model) == """{
  "description" : "desc",
  "id" : "id",
  "properties" : {
    "propK" : "propV"
  }
}"""

    where:
      val  | listVal
      null | null
      ""   | []
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
