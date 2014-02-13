package com.mangofactory.swagger.mixins
import com.wordnik.swagger.model.*

import static com.google.common.collect.Lists.newArrayList

class AuthSupport {
   def defaultAuth() {
      AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything")
      AuthorizationScope[] authorizationScopes = [authorizationScope] as AuthorizationScope[];
      List<Authorization> authorizations = [new Authorization("oauth2", authorizationScopes)];
      authorizations
   }

   def authorizationTypes(){
      def authorizationTypes = new ArrayList<AuthorizationType>()

      List<AuthorizationScope> authorizationScopeList = newArrayList();
      authorizationScopeList.add(new AuthorizationScope("global", "access all"));

      LoginEndpoint loginEndpoint = new LoginEndpoint("https://logmein.com");

      List<GrantType> grantTypes = newArrayList();
      grantTypes.add(new ImplicitGrant(loginEndpoint, "AccessToken"));

      OAuth oAuth = new OAuthBuilder()
              .scopes(authorizationScopeList)
              .grantTypes(grantTypes)
              .build();
      return oAuth
   }
}
