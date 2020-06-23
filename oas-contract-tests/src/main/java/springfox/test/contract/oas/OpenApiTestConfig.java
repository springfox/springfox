package springfox.test.contract.oas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.OAuth2Scheme;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
public class OpenApiTestConfig {
  @Bean
  public Docket petStore(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("petstore")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<String>() {{
          add("application/xml");
          add("application/json");
        }})
        .select()
        .paths(PathSelectors.regex("/.*")
                            .and(PathSelectors.regex("/error").negate())
                            .and(PathSelectors.regex("/profile").negate()))
        .build()
        .enableUrlTemplating(false)
        .host("petstore.swagger.io")
        .protocols(new HashSet<>(Arrays.asList(
            "http",
            "https")))
        .securitySchemes(Arrays.asList(
            new ApiKey("api_key", "api_key", "header"),
            HttpAuthenticationScheme.BASIC_AUTH_BUILDER
                .name("basicScheme")
                .build(),
            OAuth2Scheme.OAUTH2_IMPLICIT_FLOW_BUILDER
                .name("petstore_auth")
                .authorizationUrl("https://petstore3.swagger.io/oauth/authorize")
                .scopes(Arrays.asList(
                    new AuthorizationScope("write:pets", "Write scope"),
                    new AuthorizationScope("read:pets", "Read scope")))
                .build()));
  }
}
