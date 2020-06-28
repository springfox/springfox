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

class AuthorizationSpec extends InternalJsonSerializationSpec {
  Authorization authorization = new Authorization('atype', [new AuthorizationScope('s', 'd')] as AuthorizationScope[])

  def "should serialize"() {
    expect:
      writePretty(authorization) == """{
  "scopes" : [ {
    "description" : "d",
    "scope" : "s"
  } ],
  "type" : "atype"
}"""
  }

  def "should pass coverage"() {
    expect:
      authorization.getScopes().size() == 1
      authorization.getType() == 'atype'
  }
}
