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

package springfox.service.model

import spock.lang.Specification
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef

class ResponseMessageSpec extends Specification {
  def "ResponseMessage equals only takes the code into account" () {
    given:
      def sut = responseMessage(200, "message", "String")
    expect:
      sut.equals(test) == expectedEquality
      sut.equals(sut)
    where:
      test                                        | expectedEquality
      responseMessage(200, "message", "String")   | true
      responseMessage(200, "", "String")          | true
      responseMessage(200, "message", "")         | true
      responseMessage(201, "message", "string")   | false
  }

  def responseMessage(code, message, responseModel) {
    new ResponseMessageBuilder().code(code)
            .message(message)
            .responseModel(new ModelRef(responseModel)).build()
  }
}
