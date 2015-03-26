package springfox.documentation.swagger.mappers;

import org.mapstruct.Mapper;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.swagger.dto.ApiKey;
import springfox.documentation.swagger.dto.Authorization;
import springfox.documentation.swagger.dto.AuthorizationCodeGrant;
import springfox.documentation.swagger.dto.AuthorizationType;
import springfox.documentation.swagger.dto.BasicAuth;
import springfox.documentation.swagger.dto.ImplicitGrant;
import springfox.documentation.swagger.dto.LoginEndpoint;
import springfox.documentation.swagger.dto.TokenEndpoint;
import springfox.documentation.swagger.dto.TokenRequestEndpoint;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract springfox.documentation.swagger.dto.OAuth toSwaggerOAuth(springfox.documentation.service.OAuth from);

  public abstract BasicAuth toSwaggerBasicAuth(springfox.documentation.service.BasicAuth from);

  public abstract ApiKey toSwaggerApiKey(springfox.documentation.service.ApiKey from);

  public abstract ImplicitGrant toSwaggerImplicitGrant(springfox.documentation.service.ImplicitGrant from);

  public abstract AuthorizationCodeGrant
  toSwaggerAuthorizationCodeGrant(springfox.documentation.service.AuthorizationCodeGrant from);

  public abstract TokenEndpoint toSwaggerTokenEndpoint(springfox.documentation.service.TokenEndpoint from);

  public abstract TokenRequestEndpoint
  toSwaggerTokenRequestEndpoint(springfox.documentation.service.TokenRequestEndpoint from);

  public abstract springfox.documentation.swagger.dto.AuthorizationScope
  toSwaggerAuthorizationScope(AuthorizationScope from);

  public abstract Authorization toSwaggerAuthorization(springfox.documentation.service.Authorization from);

  public abstract LoginEndpoint toSwaggerLoginEndpoint(springfox.documentation.service.LoginEndpoint from);

  public springfox.documentation.swagger.dto.GrantType toSwaggerGrantType(
          springfox.documentation.service.GrantType from) {

    if (from instanceof springfox.documentation.service.ImplicitGrant) {
      return toSwaggerImplicitGrant((springfox.documentation.service.ImplicitGrant) from);
    } else if (from instanceof springfox.documentation.service.AuthorizationCodeGrant) {
      return toSwaggerAuthorizationCodeGrant(
              (springfox.documentation.service.AuthorizationCodeGrant) from);
    }
    throw new UnsupportedOperationException();
  }


  public AuthorizationType toSwaggerAuthorizationType(
          springfox.documentation.service.AuthorizationType from) {

    if (from instanceof springfox.documentation.service.ApiKey) {
      return toSwaggerApiKey((springfox.documentation.service.ApiKey) from);
    } else if (from instanceof springfox.documentation.service.OAuth) {
      return toSwaggerOAuth((springfox.documentation.service.OAuth) from);
    } else if (from instanceof springfox.documentation.service.BasicAuth) {
      return toSwaggerBasicAuth((springfox.documentation.service.BasicAuth) from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<springfox.documentation.swagger.dto.AuthorizationScope> toSwaggerAuthorizationScopes(
          List<AuthorizationScope> from);

  public abstract List<springfox.documentation.swagger.dto.GrantType> toSwaggerGrantTypes(List<springfox
          .documentation.service.GrantType> from);

  public abstract List<AuthorizationType> toSwaggerAuthorizationTypes(
          List<springfox.documentation.service.AuthorizationType> from);

}
