package springfox.test.contract.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ImplicitGrantBuilder
import springfox.documentation.builders.OAuthBuilder
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.AuthorizationType
import springfox.documentation.service.GrantType
import springfox.documentation.service.LoginEndpoint

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