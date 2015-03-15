package com.mangofactory.documentation.swagger.dto

class ModelPropertySpec extends InternalJsonSerializationSpec {
  final ModelPropertyDto modelProperty = new ModelPropertyDto("aName",'List[mtype]'
          , 'com.qual'
          , 1
          , true
          , 'decs'
          , new AllowableListValues())

  final ModelPropertyDto setProperty = new ModelPropertyDto("aName",'Set[mtype]'
          , 'com.qual'
          , 1
          , true
          , 'decs'
          , new AllowableListValues())

  final ModelPropertyDto regularProperty = new ModelPropertyDto("aName",'mtype'
          , 'com.qual'
          , 1
          , true
          , 'decs'
          , new AllowableListValues())

  def "should serialize lists"() {
    expect:
      writePretty(modelProperty) == '''{
  "description" : "decs",
  "required" : true,
  "type" : "array",
  "items" : {
    "type" : "mtype"
  }
}'''
  }

  def "should serialize sets"() {
    expect:
      writePretty(setProperty) == '''{
  "description" : "decs",
  "required" : true,
  "type" : "array",
  "items" : {
    "type" : "mtype"
  },
  "uniqueItems" : true
}'''
  }


  def "should serialize non lists"() {
    expect:
      writePretty(regularProperty) == '''{
  "description" : "decs",
  "required" : true,
  "type" : "mtype"
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
      }
  }

}
