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

class ModelPropertySpec extends InternalJsonSerializationSpec {
  ModelPropertyDto modelProperty = new ModelPropertyDto("aName", 'List[mtype]'
      , 'com.qual'
      , 1
      , true
      , 'decs'
      , new AllowableListValues())

  ModelPropertyDto setProperty = new ModelPropertyDto("aName", 'Set[mtype]'
      , 'com.qual'
      , 1
      , true
      , 'decs'
      , new AllowableListValues())

  ModelPropertyDto regularProperty = new ModelPropertyDto("aName", 'mtype'
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
  "items" : {
    "type" : "mtype"
  },
  "type" : "array"
}'''
  }

  def "should serialize sets"() {
    expect:
    writePretty(setProperty) == '''{
  "description" : "decs",
  "required" : true,
  "items" : {
    "type" : "mtype"
  },
  "type" : "array",
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
