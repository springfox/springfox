package com.mangofactory.swagger.models.dto.jackson

import com.mangofactory.swagger.models.dto.*

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

      AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
      grantTypes.add(authorizationCodeGrant);

      OAuth oAuth = new OAuthBuilder()
              .scopes(authorizationScopeList)
              .grantTypes(grantTypes)
              .build();

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
