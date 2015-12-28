package springfox.test.contract.swaggertests

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static com.google.common.base.Predicates.*
import static springfox.documentation.builders.PathSelectors.*

@Configuration
@EnableSwagger2
public class Swagger2TestConfig {

  @Bean
  public Docket petstore(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petstore")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
          .paths(and(regex("/api/.*"), not(regex("/api/store/search.*"))))
          .build()
        .host("petstore.swagger.io")
        .protocols(['http', 'https'] as Set)
  }

  @Bean
  public Docket petstoreWithUriTemplating(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petstoreTemplated")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
          .paths(regex("/api/store/search.*"))
          .build()
        .enableUrlTemplating(true)
        .host("petstore.swagger.io")
        .protocols(['http', 'https'] as Set)
  }

  @Bean
  public Docket business(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("businessService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/business.*"))
        .build()
  }

  @Bean
  public Docket concrete(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("concrete")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/foo/.*"))
        .build()
  }

  @Bean
  public Docket noRequestMapping(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("noRequestMapping")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/no-request-mapping/.*"))
        .build()
  }

  @Bean
  public Docket fancyPetstore(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("fancyPetstore")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/fancypets/.*"))
        .build()
  }

  @Bean
  public Docket featureService(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("featureService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/features/.*"))
        .build()
  }

  @Bean
  public Docket inheritedService(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("inheritedService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/child/.*"))
        .build()
  }

  @Bean
  public Docket pet(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .enableUrlTemplating(true)
        .select()
        .paths(regex("/pets/.*"))
        .build()
  }

  @Bean
  public Docket petGrooming(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petGroomingService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/petgrooming/.*"))
        .build()
  }

  @Bean
  public Docket root(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("root")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .ignoredParameterTypes(MetaClass)
        .select()
        .paths(regex("/.*"))
        .build()
  }

  @Bean
  public Docket groovyServiceBean(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("groovyService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .forCodeGeneration(true)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/groovy/.*"))
        .build()
        .ignoredParameterTypes(MetaClass)
  }

  @Bean
  public Docket enumServiceBean(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("enumService")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/enums/.*"))
        .build()
  }

  @Bean
  public Docket featureServiceForCodeGen(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("featureService-codeGen")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .forCodeGeneration(true)
        .produces(['application/xml', 'application/json'] as Set)
        .select()
        .paths(regex("/features/.*"))
        .build()
  }
}
