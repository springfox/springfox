package springfox.test.contract.swaggertests

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger1.annotations.EnableSwagger

import static java.util.Collections.*
import static springfox.documentation.builders.PathSelectors.*

@Configuration
@EnableSwagger
public class Swagger12TestConfig {

  @Bean
  SecurityContext securityContext() {
    def readScope = new AuthorizationScope("read:pets", "read your pets")
    def scopes = new AuthorizationScope[1]
    scopes[0] = readScope
    SecurityReference securityReference = SecurityReference.builder()
        .reference("petstore_auth")
        .scopes(scopes)
        .build()

    SecurityContext.builder()
        .securityReferences(singletonList(securityReference))
        .forPaths(ant("/petgrooming/**"))
        .build()
  }

  @Bean
  public Docket testCases(
      List<SecurityScheme> securitySchemes,
      List<SecurityContext> securityContexts) {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("default")
        .select()
          .paths(
              regex("^((?!/api).)*\$")//Not beginning with /api
              .and(regex("^\\/features\\/.*Arrays\$").negate()) //Not operations that use 2d arrays
              .and(regex("^\\/features\\/2031\$").negate()) //Not operations that use ApiImplicitParams datatype
            )
          .build()
        .securitySchemes(securitySchemes)
        .securityContexts(securityContexts)
        .ignoredParameterTypes(MetaClass)
  }
}
