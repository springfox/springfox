package com.mangofactory.swagger.models.dto

import spock.lang.Unroll

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
  @Unroll
  def "should serialize dataType[#dataType]"() {
    Parameter parameter = new Parameter("aname", "adesc", "defaultVal", false, false, dataType, null, "path", null)
    when:
      def json = writeAndParse(parameter)
    then:
      assertion.call(json)

    where:
      dataType     | assertion
      "int"        | { it.type == 'integer' && it.format == 'int32' }
      "long"       | { it.type == 'integer' && it.format == 'int64' }
      "float"      | { it.type == 'integer' && it.format == 'int64' }
      "double"     | { it.type == 'number' && it.format == 'double' }
      "Date"       | { it.type == 'string' && it.format == 'date-time' }
      "DateTime"   | { it.type == 'string' && it.format == 'date-time' }
      "string"     | { it.type == 'string' && !it.format }
      "BigDecimal" | { it.type == 'number' && !it.format }
      "BigInteger" | { it.type == 'number' && !it.format }
      "boolean"    | { it.type == 'boolean' && !it.format }
      "byte"       | { it.type == 'string' && it.format == 'byte' }
      "UUID"       | { it.type == 'string' && it.format == 'uuid' }
      "date"       | { it.type == 'string' && it.format == 'date' }
      "date-time"  | { it.type == 'string' && it.format == 'date-time' }
  }

  def "should serialize with allowable list values"() {
    expect:

      writePretty(testParameter) == """{
  "allowMultiple" : true,
  "enum" : [ "a", "b" ],
  "dataType" : "int",
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
  "dataType" : "int",
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

  def "should pass coverage"() {
    expect:
      testParameter.allowableValues
      testParameter.dataType
      testParameter.defaultValue
      testParameter.description
      testParameter.isAllowMultiple()
      testParameter.isRequired()
      testParameter.name
      testParameter.paramAccess
      testParameter.parameterType
      testParameter.paramType
  }
}
