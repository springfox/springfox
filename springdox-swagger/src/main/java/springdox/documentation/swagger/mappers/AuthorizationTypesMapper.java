package springdox.documentation.swagger.mappers;

import org.mapstruct.Mapper;
import springdox.documentation.swagger.dto.ApiKey;
import springdox.documentation.swagger.dto.Authorization;
import springdox.documentation.swagger.dto.AuthorizationCodeGrant;
import springdox.documentation.swagger.dto.AuthorizationType;
import springdox.documentation.swagger.dto.ImplicitGrant;
import springdox.documentation.swagger.dto.LoginEndpoint;
import springdox.documentation.swagger.dto.TokenEndpoint;
import springdox.documentation.swagger.dto.TokenRequestEndpoint;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract springdox.documentation.swagger.dto.OAuth toSwaggerOAuth(springdox.documentation.service.OAuth from);
  public abstract springdox.documentation.swagger.dto.BasicAuth toSwaggerBasicAuth(springdox.documentation.service.BasicAuth from);
  public abstract ApiKey toSwaggerApiKey(springdox.documentation.service.ApiKey from);
  public abstract ImplicitGrant toSwaggerImplicitGrant(springdox.documentation.service.ImplicitGrant from);
  public abstract AuthorizationCodeGrant
    toSwaggerAuthorizationCodeGrant(springdox.documentation.service.AuthorizationCodeGrant from);
  public abstract TokenEndpoint toSwaggerTokenEndpoint(springdox.documentation.service.TokenEndpoint from);
  public abstract TokenRequestEndpoint
    toSwaggerTokenRequestEndpoint(springdox.documentation.service.TokenRequestEndpoint from);
  public abstract springdox.documentation.swagger.dto.AuthorizationScope
    toSwaggerAuthorizationScope(springdox.documentation.service.AuthorizationScope from);
  public abstract Authorization toSwaggerAuthorization(springdox.documentation.service.Authorization from);
  public abstract LoginEndpoint toSwaggerLoginEndpoint(springdox.documentation.service.LoginEndpoint from);

  public springdox.documentation.swagger.dto.GrantType toSwaggerGrantType(
          springdox.documentation.service.GrantType from) {

    if (from instanceof springdox.documentation.service.ImplicitGrant) {
      return toSwaggerImplicitGrant((springdox.documentation.service.ImplicitGrant) from);
    } else if (from instanceof springdox.documentation.service.AuthorizationCodeGrant) {
      return toSwaggerAuthorizationCodeGrant(
              (springdox.documentation.service.AuthorizationCodeGrant) from);
    }
    throw new UnsupportedOperationException();
  }


  public AuthorizationType toSwaggerAuthorizationType(
          springdox.documentation.service.AuthorizationType from) {

    if (from instanceof springdox.documentation.service.ApiKey) {
      return toSwaggerApiKey((springdox.documentation.service.ApiKey)from);
    } else if (from instanceof springdox.documentation.service.OAuth) {
      return toSwaggerOAuth((springdox.documentation.service.OAuth)from);
    } else if (from instanceof springdox.documentation.service.BasicAuth) {
      return toSwaggerBasicAuth((springdox.documentation.service.BasicAuth)from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<springdox.documentation.swagger.dto.AuthorizationScope> toSwaggerAuthorizationScopes(
          List<springdox.documentation.service.AuthorizationScope> from);

  public abstract List<springdox.documentation.swagger.dto.GrantType> toSwaggerGrantTypes(List<springdox.documentation.service.GrantType> from);

  public abstract List<AuthorizationType> toSwaggerAuthorizationTypes(
          List<springdox.documentation.service.AuthorizationType> from);

}
