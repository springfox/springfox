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

package springfox.documentation.swagger.jackson

import springfox.documentation.swagger.dto.AuthorizationCodeGrant
import springfox.documentation.swagger.dto.AuthorizationScope
import springfox.documentation.swagger.dto.ImplicitGrant
import springfox.documentation.swagger.dto.InternalJsonSerializationSpec
import springfox.documentation.swagger.dto.LoginEndpoint
import springfox.documentation.swagger.dto.OAuth
import springfox.documentation.swagger.dto.GrantType
import springfox.documentation.swagger.dto.TokenEndpoint
import springfox.documentation.swagger.dto.TokenRequestEndpoint

class AuthorizationTypesSerializerSpec extends InternalJsonSerializationSpec {

  def "should serialize AuthorizationTypesSerializer"() {
    setup:
      def authorizationScopeList = []
      authorizationScopeList << new AuthorizationScope("email", "access all")

      List<GrantType> grantTypes = []
      LoginEndpoint loginEndpoint = new LoginEndpoint("http://petstore.swagger.wordnik.com/oauth/dialog");
      grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));

      TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("http://petstore.swagger.wordnik.com/oauth/requestToken", "client_id", "client_secret");
      TokenEndpoint tokenEndpoint = new TokenEndpoint("http://petstore.swagger.wordnik.com/oauth/token", "auth_code");

      AuthorizationCodeGrant authorizationCodeGrant =  new AuthorizationCodeGrant()
      authorizationCodeGrant.tokenRequestEndpoint = tokenRequestEndpoint
      authorizationCodeGrant.tokenEndpoint = tokenEndpoint

      grantTypes.add(authorizationCodeGrant);

      OAuth oAuth = new OAuth()
      oAuth.scopes = authorizationScopeList
      oAuth.grantTypes = grantTypes

    when:
      def json = writeAndParse(oAuth)

    then:
      json.type == 'oauth2'
      json.scopes[0].scope == 'email'
      json.scopes[0].description == 'access all'

      json.grantTypes.implicit.loginEndpoint.url == 'http://petstore.swagger.wordnik.com/oauth/dialog'
      json.grantTypes.implicit.tokenName == 'access_token'

      json.grantTypes.authorization_code.tokenRequestEndpoint.url == "http://petstore.swagger.wordnik.com/oauth/requestToken"
      json.grantTypes.authorization_code.tokenRequestEndpoint.clientIdName == "client_id"
      json.grantTypes.authorization_code.tokenRequestEndpoint.clientSecretName == "client_secret"
  }
}
