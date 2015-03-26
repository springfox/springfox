package springfox.documentation.spring.web.authorization
import spock.lang.Specification
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.service.contexts.AuthorizationContext

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
