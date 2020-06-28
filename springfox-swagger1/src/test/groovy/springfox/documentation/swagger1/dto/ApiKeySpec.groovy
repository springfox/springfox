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

class ApiKeySpec extends InternalJsonSerializationSpec {

  ApiKey headerKey = new ApiKey('myKey')
  ApiKey customKey = new ApiKey('myKey', 'cookie')

  def "should produce a header key"() {
    expect:
    writePretty(headerKey) ==
        """{
  "keyname" : "myKey",
  "passAs" : "header",
  "type" : "apiKey"
}"""
  }

  def "should produce a custom key"() {
    expect:
    writePretty(customKey) ==
        """{
  "keyname" : "myKey",
  "passAs" : "cookie",
  "type" : "apiKey"
}"""
  }

  def "should pass coverage"() {
    expect:
    customKey.getKeyname() == 'myKey'
    customKey.getPassAs() == 'cookie'
    customKey.getType() == 'apiKey'
  }
}
