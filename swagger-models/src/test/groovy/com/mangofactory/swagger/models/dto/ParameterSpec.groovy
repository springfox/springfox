package com.mangofactory.swagger.models.dto

class ParameterSpec extends InternalJsonSerializationSpec {

  final Parameter testParameter = new Parameter("aname",
          "adesc",
          "defaultVal",
          true,
          true,
          'int',
          new AllowableListValues(['a', 'b'], 'string'),
          "path",
          'all')

  def "should serialize with allowable list values"() {
    expect:

      writePretty(testParameter) == """{
  "allowMultiple" : true,
  "enum" : [ "a", "b" ],
  "defaultValue" : "defaultVal",
  "description" : "adesc",
  "name" : "aname",
  "paramAccess" : "all",
  "paramType" : "path",
  "format" : "int32",
  "type" : "integer",
  "required" : true
}"""
  }


  def "should serialize with allowable range values"() {
    expect:
      Parameter parameter = new Parameter("aname",
              "adesc",
              "2",
              true,
              true,
              'int',
              new AllowableRangeValues('1', '2'),
              "path",
              'all')
      writePretty(parameter) == """{
  "allowMultiple" : true,
  "maximum" : "2",
  "minimum" : "1",
  "defaultValue" : "2",
  "description" : "adesc",
  "name" : "aname",
  "paramAccess" : "all",
  "paramType" : "path",
  "format" : "int32",
  "type" : "integer",
  "required" : true
}"""
  }

  def "should override body param name"() {
    expect:
      Parameter parameter = new Parameter("aname",
              "adesc",
              "2",
              true,
              true,
              'int',
              new AllowableRangeValues('1', '2'),
              "body",
              'all')
      writePretty(parameter) == """{
  "allowMultiple" : true,
  "maximum" : "2",
  "minimum" : "1",
  "defaultValue" : "2",
  "description" : "adesc",
  "name" : "body",
  "paramAccess" : "all",
  "paramType" : "body",
  "format" : "int32",
  "type" : "integer",
  "required" : true
}"""
  }

  def "array types are unwrapped"() {
    expect:
      Parameter parameter = new Parameter("aname",
              "adesc",
              "2",
              true,
              true,
              'Set[Pet]',
              null,
              "body",
              'all')
      writePretty(parameter) == """{
  "allowMultiple" : true,
  "defaultValue" : "2",
  "description" : "adesc",
  "name" : "body",
  "paramAccess" : "all",
  "paramType" : "body",
  "type" : "array",
  "items" : {
    "type" : "Pet"
  },
  "uniqueItems" : true,
  "required" : true
}"""
  }

  def "should pass coverage"() {
    expect:
      testParameter.allowableValues
      testParameter.defaultValue
      testParameter.description
      testParameter.isAllowMultiple()
      testParameter.isRequired()
      testParameter.name
      testParameter.paramAccess
      testParameter.paramType
  }
}
