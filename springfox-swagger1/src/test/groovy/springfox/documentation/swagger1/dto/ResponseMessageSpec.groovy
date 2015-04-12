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

class ResponseMessageSpec extends InternalJsonSerializationSpec {

  @Unroll
  def "should serialize"() {
    expect:
      writePretty(responseMessage) == expected

    where:
      responseMessage << [
              new ResponseMessage(200, "ok", null),
              new ResponseMessage(200, "ok", "model")
      ]

      expected << [
              """{
  "code" : 200,
  "message" : "ok"
}""",
              """{
  "code" : 200,
  "message" : "ok",
  "responseModel" : "model"
}"""
      ]

  }

  def "should pass coverage"() {
    expect:
      def message = new ResponseMessage(200, "ok", 'model')
      message.getCode()
      message.getMessage()
      message.getResponseModel()
  }
}
