package springfox.test.contract.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.RecursiveAlternateTypeRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.*;

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
        .protocols(new HashSet<>(Arrays.asList("http", "https")))
        .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
            Arrays.asList(
                newRule(resolver.resolve(Mono.class, WildcardType.class), resolver.resolve(WildcardType.class)),
                newRule(resolver.resolve(ResponseEntity.class, WildcardType.class),
                    resolver.resolve(WildcardType.class)))))
        .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
            Arrays.asList(
                newRule(resolver.resolve(Flux.class, WildcardType.class),
                    resolver.resolve(List.class, WildcardType.class)),
                newRule(resolver.resolve(ResponseEntity.class, WildcardType.class),
                    resolver.resolve(WildcardType.class)))));
  }

  @Autowired
  private TypeResolver resolver;
}
