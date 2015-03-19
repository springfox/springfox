package springdox.documentation.spring.web.authorization
import spock.lang.Specification
import springdox.documentation.builders.PathSelectors
import springdox.documentation.spi.service.contexts.AuthorizationContext
import springdox.documentation.spring.web.mixins.AuthSupport

@Mixin(AuthSupport)
class AuthorizationContextSpec extends Specification {

   def "scala authorizations"() {
    given:
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(auth)
              .forPaths(PathSelectors.any())
              .build()
      authorizationContext.scalaAuthorizations
    expect:
      authorizationContext.getScalaAuthorizations().size() == expected

    where:
      auth          | expected
      defaultAuth() | 1
      []            | 0
   }

}
