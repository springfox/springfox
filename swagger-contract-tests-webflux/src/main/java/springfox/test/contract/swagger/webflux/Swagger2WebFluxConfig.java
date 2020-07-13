package springfox.test.contract.swagger.webflux;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@Import({springfox.petstore.webflux.PetStoreConfiguration.class})
@ComponentScan({
    "springfox.test.contract.swagger",
    "springfox.petstore.webflux.controller"
})
public class Swagger2WebFluxConfig {

  @Bean
  public Docket petstoreWithUriTemplating(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petstoreTemplated")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList("application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/api/store/search.*"))
        .build()
        .enableUrlTemplating(true)
        .host("petstore.swagger.io")
        .protocols(new HashSet<>(Arrays.asList("http", "https")));
  }

  @Bean
  public Docket features(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("features")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList("application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/features/.*"))
        .build()
        .enableUrlTemplating(true)
        .host("petstore.swagger.io")
        .protocols(new HashSet<>(Arrays.asList("http", "https")));
  }

  @Bean
  public Docket bugs(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("bugs")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList("application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/bugs/.*"))
        .build()
        .enableUrlTemplating(true)
        .host("petstore.swagger.io")
        .protocols(new HashSet<>(Arrays.asList("http", "https")));
  }

}
