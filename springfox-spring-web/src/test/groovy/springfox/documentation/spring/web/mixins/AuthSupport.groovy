/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.spring.web.mixins

import springfox.documentation.service.AuthorizationCodeGrant
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.GrantType
import springfox.documentation.service.ImplicitGrant
import springfox.documentation.service.LoginEndpoint
import springfox.documentation.service.OAuth
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.TokenEndpoint
import springfox.documentation.service.TokenRequestEndpoint

import static java.util.Collections.*

trait AuthSupport {
  def defaultAuth() {
    AuthorizationScope authorizationScope =
            new AuthorizationScope("global", "accessEverything")
    AuthorizationScope[] authorizationScopes = [authorizationScope] as AuthorizationScope[];
    singletonList(new SecurityReference("oauth2", authorizationScopes))
  }

  def authorizationTypes() {

    List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
    authorizationScopeList.add(new AuthorizationScope("global", "access all"));


    List<GrantType> grantTypes = new ArrayList<>();

    LoginEndpoint loginEndpoint = new LoginEndpoint("http://petstore.swagger.io/oauth/dialog");
    grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));


    TokenRequestEndpoint tokenRequestEndpoint =
            new TokenRequestEndpoint("http://petstore.swagger.io/oauth/requestToken", "client_id", "client_secret")
    TokenEndpoint tokenEndpoint =
            new TokenEndpoint("http://petstore.swagger.io/oauth/token", "auth_code")

    AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint)

    grantTypes.add(authorizationCodeGrant)

    OAuth oAuth = new OAuth("oauth", authorizationScopeList, grantTypes)
    return oAuth
  }

  def assertDefaultAuth(json) {
    def oauth2 = json.authorizations.get('oauth2')

    assert oauth2.type == "oauth2"
    assert oauth2.scopes[0].scope == "global"
    assert oauth2.scopes[0].description == "access all"

    def implicit = oauth2.grantTypes.implicit
    assert implicit.loginEndpoint.url == "http://petstore.swagger.io/oauth/dialog"
    assert implicit.tokenName == "access_token"

    def tokenRequestEndpoint = oauth2.grantTypes.authorization_code.tokenRequestEndpoint
    assert tokenRequestEndpoint.url == 'http://petstore.swagger.io/oauth/requestToken'
    assert tokenRequestEndpoint.clientIdName == 'client_id'
    assert tokenRequestEndpoint.clientSecretName == 'client_secret'

    def tokenEndpoint = oauth2.grantTypes.authorization_code.tokenEndpoint
    assert tokenEndpoint.url == 'http://petstore.swagger.io/oauth/token'
    assert tokenEndpoint.tokenName == 'auth_code'
    true
  }
}
