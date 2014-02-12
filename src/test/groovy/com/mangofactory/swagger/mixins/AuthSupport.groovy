package com.mangofactory.swagger.mixins

import com.wordnik.swagger.model.Authorization
import com.wordnik.swagger.model.AuthorizationScope


class AuthSupport {
   def defaultAuth() {
      AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything")
      AuthorizationScope[] authorizationScopes = [authorizationScope] as AuthorizationScope[];
      List<Authorization> authorizations = [new Authorization("oauth2", authorizationScopes)];
      authorizations
   }
}
