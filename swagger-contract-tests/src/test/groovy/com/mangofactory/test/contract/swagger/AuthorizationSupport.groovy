package com.mangofactory.test.contract.swagger
import com.mangofactory.documentation.builders.ImplicitGrantBuilder
import com.mangofactory.documentation.builders.OAuthBuilder
import com.mangofactory.documentation.service.ApiKey
import com.mangofactory.documentation.service.AuthorizationScope
import com.mangofactory.documentation.service.AuthorizationType
import com.mangofactory.documentation.service.GrantType
import com.mangofactory.documentation.service.LoginEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import static com.google.common.collect.Lists.*

@Configuration
public class AuthorizationSupport {
  @Bean
  AuthorizationType oauth() {
    new OAuthBuilder()
            .name("petstore_auth")
            .grantTypes(grantTypes())
            .scopes(scopes())
            .build()
  }

  @Bean
  AuthorizationType apiKey() {
    new ApiKey("api_key", "api_key", "header")
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