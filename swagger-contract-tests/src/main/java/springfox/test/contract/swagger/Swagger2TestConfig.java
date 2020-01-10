package springfox.test.contract.swagger;

import com.fasterxml.classmate.TypeResolver;
import groovy.lang.MetaClass;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Configuration
@EnableSwagger2WebMvc
public class Swagger2TestConfig {
  @Bean
  public Docket petstoreWithUriTemplating(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
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
    return new Docket(DocumentationType.SWAGGER_2)
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
    return new Docket(DocumentationType.SWAGGER_2)
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
    return new Docket(DocumentationType.SWAGGER_2)
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
    return new Docket(DocumentationType.SWAGGER_2)
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
  public Docket featureService(TypeResolver resolver, List<SecurityScheme> authorizationTypes) {
    // tag::question-27-config[]
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("featureService")
        .useDefaultResponseMessages(false)
        .additionalModels(resolver.resolve(FeatureDemonstrationService.CustomTypeFor2031.class))
        .securitySchemes(authorizationTypes)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .alternateTypeRules(AlternateTypeRules.newRule(
            LocalDate.class,
            String.class))
        .select().paths(PathSelectors.regex("/features/.*"))
        .build();
  }

  @Bean
  public Docket inheritedService(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
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
    return new Docket(DocumentationType.SWAGGER_2)
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
  public Docket bugs(TypeResolver resolver, List<SecurityScheme> authorizationTypes) {
    AuthorizationScope[] scopes =
        new AuthorizationScope[] {
            new AuthorizationScopeBuilder()
                .scope("read")
                .description("Read access")
                .build()
        };
    return new Docket(DocumentationType.SWAGGER_2).groupName("bugs")
        .apiInfo(new ApiInfoBuilder().version("1.0")
                     .title("bugs API")
                     .description("bugs API")
                     .extensions(Collections.singletonList(new StringVendorExtension(
                         "test",
                         "testValue")))
                     .build())
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .tags(new Tag(
            "foo",
            "Foo Description"))
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .enableUrlTemplating(true)
        .securityContexts(Collections.singletonList(
            SecurityContext.builder()
                .securityReferences(
                    Collections.singletonList(
                        new SecurityReference(
                            "petstore_auth",
                            scopes)))
                .forPaths(PathSelectors.regex("/bugs/2268"))
                .forHttpMethods(Predicate.isEqual(HttpMethod.GET))
                .build()))
        .alternateTypeRules(
            AlternateTypeRules.newRule(
                URL.class,
                String.class),
            AlternateTypeRules.newRule(
                resolver.resolve(
                    List.class,
                    Link.class),
                resolver.resolve(
                    Map.class,
                    String.class,
                    BugsController.LinkAlternate.class)))
        .directModelSubstitute(
            ByteBuffer.class,
            String.class)
        .select()
        .paths(PathSelectors.regex("/bugs/.*"))
        .build();
  }

  @Bean
  public Docket bugsDifferent(TypeResolver resolver, List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2).groupName("bugsDifferent")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .tags(new Tag(
            "foo",
            "Foo Description"))
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .enableUrlTemplating(true)
        .alternateTypeRules(
            AlternateTypeRules.newRule(
                URL.class,
                String.class),
            AlternateTypeRules.newRule(
                resolver.resolve(
                    List.class,
                    Link.class),
                resolver.resolve(
                    Map.class,
                    String.class,
                    BugsController.LinkAlternate.class)))
        .directModelSubstitute(
            ByteBuffer.class,
            String.class)
        .ignoredParameterTypes(
            BugsController.Bug1627.class,
            BugsController.Lang.class)
        .select()
        .paths(PathSelectors.regex("/bugs/.*"))
        .build();
  }

  @Bean
  public Docket differentGroup(TypeResolver resolver) {
    return new Docket(DocumentationType.SWAGGER_2).groupName("different-group")
        .useDefaultResponseMessages(false)
        .tags(new Tag(
            "Different",
            "Different Group"))
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .enableUrlTemplating(true)
        .alternateTypeRules(
            AlternateTypeRules.newRule(
                URL.class,
                String.class),
            AlternateTypeRules.newRule(
                resolver.resolve(
                    List.class,
                    Link.class),
                resolver.resolve(
                    Map.class,
                    String.class,
                    BugsController.LinkAlternate
                        .class)))
        .directModelSubstitute(
            ByteBuffer.class,
            String.class)
        .select()
        .paths(PathSelectors.regex("/different/.*"))
        .build();
  }

  @Bean
  public Docket petGrooming(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2).groupName("petGroomingService")
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
    return new Docket(DocumentationType.SWAGGER_2).groupName("root")
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
    return new Docket(DocumentationType.SWAGGER_2).groupName("groovyService")
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
    return new Docket(DocumentationType.SWAGGER_2).groupName("enumService")
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
  public Docket featureServiceForCodeGen(
      TypeResolver resolver,
      List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2).groupName("featureService-codeGen")
        .additionalModels(resolver.resolve(FeatureDemonstrationService.CustomTypeFor2031.class))
        .
            useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .forCodeGeneration(true)
        .produces(new HashSet<>(
            Arrays.asList(
                "application/xml",
                "application/json")))
        .select()
        .paths(PathSelectors.regex("/features/.*"))
        .build();
  }

  @Bean
  public Docket consumesProducesNotOnDocumentContext(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2).groupName("consumesProducesNotOnDocumentContext")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .select()
        .paths(PathSelectors.regex("/consumes-produces/.*"))
        .build();
  }

  @Bean
  public Docket consumesProducesOnDocumentContext(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
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
  public ApiListingScannerPlugin listingScanner(CachingOperationNameGenerator operationNames) {
    return new Bug1767ListingScanner(operationNames);
  }

  @Bean
  public Docket springDataRest() {
    return new Docket(DocumentationType.SWAGGER_2).groupName("spring-data-rest")
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
    return new Docket(DocumentationType.SWAGGER_2).groupName("same")
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
