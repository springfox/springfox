package springfox.documentation.swagger2.mappers

import io.swagger.models.auth.OAuth2Definition
import spock.lang.Specification
import springfox.documentation.service.AuthorizationCodeGrant
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.ClientCredentialsGrant
import springfox.documentation.service.GrantType
import springfox.documentation.service.OAuth
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant
import springfox.documentation.service.SecurityScheme
import springfox.documentation.service.TokenEndpoint
import springfox.documentation.service.TokenRequestEndpoint

import static java.util.Collections.*

class OAuth2AuthFactorySpec extends Specification {
  def "Maps authorization code grants" () {
    given:
      List<GrantType> grants = singletonList(
          new AuthorizationCodeGrant(
              new TokenRequestEndpoint("tre:uri", "treClient", "tre"),
              new TokenEndpoint("te:uri", "treToken")))
      List<AuthorizationScope> scopes = new ArrayList<>()
      SecurityScheme security = new OAuth("oauth", new ArrayList(scopes), new ArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefinition = factory.create(security)
    then:
      securityDefinition.type == "oauth2"
      ((OAuth2Definition)securityDefinition).getFlow() == "accessCode"
      ((OAuth2Definition)securityDefinition).tokenUrl == "te:uri"
      ((OAuth2Definition)securityDefinition).authorizationUrl == "tre:uri"
  }

  def "Maps application grant" () {
    given:
      List<GrantType> grants = singletonList(new ClientCredentialsGrant("token:uri"))
      List<AuthorizationScope> scopes = new ArrayList<>()
      SecurityScheme security = new OAuth("oauth", new ArrayList(scopes), new ArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefinition = factory.create(security)
    then:
      securityDefinition.type == "oauth2"
      ((OAuth2Definition)securityDefinition).getFlow() == "application"
      ((OAuth2Definition)securityDefinition).tokenUrl == "token:uri"
  }

  def "Maps password grant" () {
    given:
      List<GrantType> grants = singletonList(new ResourceOwnerPasswordCredentialsGrant("token:uri"))
      List<AuthorizationScope> scopes = new ArrayList<>()
      SecurityScheme security = new OAuth("oauth", new ArrayList(scopes), new ArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      def securityDefinition = factory.create(security)
    then:
      securityDefinition.type == "oauth2"
      ((OAuth2Definition)securityDefinition).getFlow() == "password"
      ((OAuth2Definition)securityDefinition).tokenUrl == "token:uri"
  }

  def "Throws exception when it receives an unknown grant" () {
    given:
      List<GrantType> grants = singletonList(new GrantType("unknown"))
      List<AuthorizationScope> scopes = new ArrayList<>()
      SecurityScheme security = new OAuth("oauth", new ArrayList(scopes), new ArrayList(grants))
    and:
      OAuth2AuthFactory factory = new OAuth2AuthFactory()
    when:
      factory.create(security)
    then:
      thrown(IllegalArgumentException)
  }
}
