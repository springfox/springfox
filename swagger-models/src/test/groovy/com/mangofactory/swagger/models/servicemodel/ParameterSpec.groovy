package com.mangofactory.swagger.models.servicemodel

import com.mangofactory.swagger.models.servicemodel.builder.ParameterBuilder

class ParameterSpec extends InternalJsonSerializationSpec {

  final Parameter testParameter = new ParameterBuilder()
          .name('aname')
          .description('adesc')
          .defaultValue('defaultVal')
          .required(true)
          .allowMultiple(true)
          .dataType("int")
          .allowableValues(new AllowableListValues(['a', 'b'], 'string'))
          .parameterType("path")
          .parameterAccess("all")
          .build()

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
      Parameter parameter = new ParameterBuilder()
              .name('aname')
              .description('adesc')
              .defaultValue('2')
              .required(true)
              .allowMultiple(true)
              .dataType("int")
              .allowableValues(new AllowableRangeValues('1', '2'))
              .parameterType("path")
              .parameterAccess("all")
              .build()

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

      Parameter parameter = new ParameterBuilder()
              .name('aname')
              .description('adesc')
              .defaultValue('2')
              .required(true)
              .allowMultiple(true)
              .dataType("int")
              .allowableValues(new AllowableRangeValues('1', '2'))
              .parameterType("body")
              .parameterAccess("all")
              .build()

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
      Parameter parameter = new ParameterBuilder()
              .name('aname')
              .description('adesc')
              .defaultValue('2')
              .required(true)
              .allowMultiple(true)
              .dataType("Set[Pet]")
              .allowableValues(null)
              .parameterType("body")
              .parameterAccess("all")
              .build()

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
