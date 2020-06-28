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

import spock.lang.Shared

class TokenRequestEndpointSpec extends InternalJsonSerializationSpec {

  @Shared
  String URL = "http://petstore.swagger.io/oauth/requestToken"
  @Shared
  TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint(
      URL,
      "client_id",
      "client_secret"
  )

  def "should serialize"() {
    expect:
    writePretty(tokenRequestEndpoint) == """{
  "clientIdName" : "client_id",
  "clientSecretName" : "client_secret",
  "url" : "http://petstore.swagger.io/oauth/requestToken"
}"""
  }

  def "should pass coverage"() {
    expect:
    tokenRequestEndpoint.clientIdName == 'client_id'
    tokenRequestEndpoint.clientSecretName == 'client_secret'
    tokenRequestEndpoint.url == URL
  }
}
