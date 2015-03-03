package com.mangofactory.test.contract.swagger

import com.mangofactory.documentation.builders.ImplicitGrantBuilder
import com.mangofactory.documentation.builders.OAuthBuilder
import com.mangofactory.documentation.service.ApiKey
import com.mangofactory.documentation.service.AuthorizationScope
import com.mangofactory.documentation.service.AuthorizationType
import com.mangofactory.documentation.service.GrantType
import com.mangofactory.documentation.service.LoginEndpoint

import static com.google.common.collect.Lists.*

trait AuthorizationSupport {
  List<AuthorizationType> authTypes() {
    List<AuthorizationType> authTypes = newArrayList();
    authTypes.add(new OAuthBuilder()
            .name("petstore_auth")
            .grantTypes(grantTypes())
            .scopes(scopes())
            .build())
    authTypes.add(new ApiKey("api_key", "api_key", "header"))
    return authTypes
  }

  List<AuthorizationScope> scopes() {
    newArrayList(new AuthorizationScope("write:pets", "modify pets in your account"),
    new AuthorizationScope("read:pets", "read your pets"))
  }

  List<GrantType> grantTypes() {
    newArrayList(new ImplicitGrantBuilder()
            .loginEndpoint(new LoginEndpoint("http://petstore.swagger.io/api/oauth/dialog"))
            .build()) 
  }
}