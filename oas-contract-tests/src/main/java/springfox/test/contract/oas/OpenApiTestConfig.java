package springfox.test.contract.oas;

import groovy.lang.MetaClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.oas.annotations.EnableOasWebMvc;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@EnableOasWebMvc
public class OpenApiTestConfig {
  @Bean
  public Docket petstoreWithUriTemplating(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("petstoreTemplated")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<String>() {{
          add("application/xml");
          add("application/json");
        }})
        .select()
        .paths(PathSelectors.regex("/api/store/search.*"))
        .build()
        .enableUrlTemplating(true)
        .host("petstore.swagger.io")
        .protocols(new HashSet<>(Arrays.asList(
            "http",
            "https")));
  }

  @Bean
  public Docket business(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("businessService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/business.*"))
        .build();
  }

  @Bean
  public Docket concrete(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("concrete")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/foo/.*"))
        .build();
  }

  @Bean
  public Docket noRequestMapping(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("noRequestMapping")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/no-request-mapping/.*"))
        .build();
  }

  @Bean
  public Docket fancyPetstore(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("fancyPetstore")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/fancypets/.*"))
        .build();
  }

  @Bean
  public Docket inheritedService(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("inheritedService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .select()
        .paths(PathSelectors.regex("/child/.*"))
        .build();
  }

  @Bean
  public Docket pet(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("petService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(Arrays.asList(
            "application/xml",
            "application/json")))
        .enableUrlTemplating(true)
        .select()
        .paths(PathSelectors.regex("/pets/.*"))
        .build();
  }

  @Bean
  public Docket petGrooming(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("petGroomingService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/petgrooming/.*"))
        .build();
  }

  @Bean
  public Docket root(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("root")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .ignoredParameterTypes(MetaClass.class)
        .select()
        .paths(PathSelectors.regex("/.*"))
        .build();
  }

  @Bean
  public Docket groovyServiceBean(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("groovyService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .forCodeGeneration(true)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/groovy/.*"))
        .build()
        .ignoredParameterTypes(MetaClass.class);
  }

  @Bean
  public Docket enumServiceBean(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("enumService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/enums/.*"))
        .build();
  }

  @Bean
  public Docket consumesProducesNotOnDocumentContext(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("consumesProducesNotOnDocumentContext")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .select()
        .paths(PathSelectors.regex("/consumes-produces/.*"))
        .build();
  }

  @Bean
  public Docket consumesProducesOnDocumentContext(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("consumesProducesOnDocumentContext")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .consumes(new HashSet<String>() {{
          add("text/plain");
        }})
        .produces(new HashSet<String>() {{
          add("application/json");
        }})
        .select().paths(PathSelectors.regex("/consumes-produces/.*")).build();
  }

  @Bean
  public Docket springDataRest() {
    return new Docket(DocumentationType.OAS_30)
        .groupName("spring-data-rest")
        .useDefaultResponseMessages(false)
        .enableUrlTemplating(true)
        .securitySchemes(new ArrayList<>())
        .forCodeGeneration(true)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/rest/people.*")
                            .or(PathSelectors.regex("/rest/tags.*"))
                            .or(PathSelectors.regex("/rest/categories.*"))
                            .or(PathSelectors.regex("/rest/addresses.*")))
        .build();
  }

  @Bean
  public Docket same(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.OAS_30)
        .groupName("same")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/same/.*"))
        .build();
  }
}
