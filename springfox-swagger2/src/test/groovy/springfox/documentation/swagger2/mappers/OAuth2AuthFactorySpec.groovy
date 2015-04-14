package springfox.documentation.swagger2.mappers

import com.wordnik.swagger.models.auth.OAuth2Definition
import spock.lang.Specification
import springfox.documentation.service.AuthorizationCodeGrant
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.GrantType
import springfox.documentation.service.OAuth
import springfox.documentation.service.SecurityScheme
import springfox.documentation.service.TokenEndpoint
import springfox.documentation.service.TokenRequestEndpoint

import static com.google.common.collect.Lists.*

class OAuth2AuthFactorySpec extends Specification {
  def "Maps authorization code grants" () {
    given:
      List<GrantType> grants = newArrayList(
          new AuthorizationCodeGrant(
              new TokenRequestEndpoint("tre:uri", "treClient", "tre"),
              new TokenEndpoint("te:uri", "treToken")))
      List<AuthorizationScope> scopes = newArrayList()
      SecurityScheme security = new OAuth("oauth", newArrayList(scopes), newArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefintion = factory.create(security)
    then:
      securityDefintion.type == "oauth2"
      ((OAuth2Definition)securityDefintion).getFlow() == "accessCode"
      ((OAuth2Definition)securityDefintion).tokenUrl == "te:uri"
      ((OAuth2Definition)securityDefintion).authorizationUrl == "tre:uri"
  }

  def "Maps application grant" () {
    given:
      List<GrantType> grants = newArrayList(new GrantType("application"))
      List<AuthorizationScope> scopes = newArrayList()
      SecurityScheme security = new OAuth("oauth", newArrayList(scopes), newArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefintion = factory.create(security)
    then:
      securityDefintion.type == "oauth2"
      ((OAuth2Definition)securityDefintion).getFlow() == "application"
  }

  def "Maps password grant" () {
    given:
      List<GrantType> grants = newArrayList(new GrantType("password"))
      List<AuthorizationScope> scopes = newArrayList()
      SecurityScheme security = new OAuth("oauth", newArrayList(scopes), newArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefintion = factory.create(security)
    then:
      securityDefintion.type == "oauth2"
      ((OAuth2Definition)securityDefintion).getFlow() == "password"
  }

  def "Throws exception when it receives an unknown grant" () {
    given:
      List<GrantType> grants = newArrayList(new GrantType("unknown"))
      List<AuthorizationScope> scopes = newArrayList()
      SecurityScheme security = new OAuth("oauth", newArrayList(scopes), newArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      factory.create(security)
    then:
      thrown(IllegalArgumentException)
  }
}
