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

import spock.lang.Unroll

class ModelSpec extends InternalJsonSerializationSpec {

  ModelDto model = new ModelDto("id", "name", 'qtype',
      ['propK': 'propV'] as Map<String, ModelPropertyDto>,
      'desc', 'bModel', 'discrim',
      ['subtype1'])

  def "should serialize"() {
    expect:
    writePretty(model) == """{
  "baseModel" : "bModel",
  "description" : "desc",
  "discriminator" : "discrim",
  "id" : "name",
  "properties" : {
    "propK" : "propV"
  },
  "subTypes" : [ "subtype1" ]
}"""
  }

  @Unroll
  def "should serialize ignoring optional fields"() {
    final ModelDto model = new ModelDto("id", "name", 'qtype',
        ['propK': 'propV'] as Map<String, ModelPropertyDto>, 'desc', val, val, listVal)

    expect:
    writePretty(model) == """{
  "description" : "desc",
  "id" : "name",
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
