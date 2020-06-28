/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.dto

class ParameterSpec extends InternalJsonSerializationSpec {

  Parameter testParameter = new Parameter('aname'
      , 'adesc'
      , 'defaultVal'
      , true
      , true
      , "int"
      , new AllowableListValues(['a', 'b'], 'string')
      , "path"
      , "all")

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
    Parameter parameter = new Parameter('aname'
        , 'adesc'
        , '2'
        , true
        , true
        , "int"
        , new AllowableRangeValues('1', '2')
        , "path"
        , "all")

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

    Parameter parameter = new Parameter('aname'
        , 'adesc'
        , '2'
        , true
        , true
        , "int"
        , new AllowableRangeValues('1', '2')
        , "body"
        , "all")

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
    Parameter parameter = new Parameter('aname'
        , 'adesc'
        , '2'
        , true
        , true
        , "Set[Pet]"
        , null
        , "body"
        , "all")

    writePretty(parameter) == """{
  "allowMultiple" : true,
  "defaultValue" : "2",
  "description" : "adesc",
  "name" : "body",
  "paramAccess" : "all",
  "paramType" : "body",
  "items" : {
    "type" : "Pet"
  },
  "type" : "array",
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
