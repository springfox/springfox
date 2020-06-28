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

class OperationSpec extends InternalJsonSerializationSpec {

  Operation operation = new Operation('PUT'
      , 'updatePet'
      , 'updatePet'
      , 'void'
      , 'updatePetUsingPUT'
      , 0
      , ["*/*"]
      , ['application/json']
      , []
      , []
      , [new Parameter('pet', 'pet', '', false, false, 'Pet', null, 'body', null)]
      , [new ResponseMessage(200, 'ok', null)] as Set
      , "false"
  )

  def "should serialize an operation"() {
    expect:
    writePretty(operation) == '''{
  "method" : "PUT",
  "summary" : "updatePet",
  "notes" : "updatePet",
  "nickname" : "updatePetUsingPUT",
  "produces" : [ "*/*" ],
  "consumes" : [ "application/json" ],
  "parameters" : [ {
    "allowMultiple" : false,
    "defaultValue" : "",
    "description" : "pet",
    "name" : "body",
    "paramType" : "body",
    "type" : "Pet",
    "required" : false
  } ],
  "responseMessages" : [ {
    "code" : 200,
    "message" : "ok"
  } ],
  "deprecated" : "false",
  "type" : "void"
}'''
  }

  def "should pass coverage"() {
    expect:
    operation.with {
      getAuthorizations()
      getConsumes()
      getDeprecated()
      getMethod()
      getNickname()
      getNotes()
      getParameters()
      getPosition()
      getProduces()
      getProtocol()
      getResponseMessages()
      getSummary()
    }
  }
}
