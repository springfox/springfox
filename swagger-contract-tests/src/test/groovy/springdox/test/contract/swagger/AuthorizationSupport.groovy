package springdox.test.contract.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springdox.documentation.builders.ImplicitGrantBuilder
import springdox.documentation.builders.OAuthBuilder
import springdox.documentation.service.ApiKey
import springdox.documentation.service.AuthorizationScope
import springdox.documentation.service.AuthorizationType
import springdox.documentation.service.GrantType
import springdox.documentation.service.LoginEndpoint

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