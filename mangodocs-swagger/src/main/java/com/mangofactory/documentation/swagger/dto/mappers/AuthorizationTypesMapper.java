package com.mangofactory.documentation.swagger.dto.mappers;

import com.mangofactory.documentation.swagger.dto.ApiKey;
import com.mangofactory.documentation.swagger.dto.Authorization;
import com.mangofactory.documentation.swagger.dto.AuthorizationCodeGrant;
import com.mangofactory.documentation.swagger.dto.AuthorizationScope;
import com.mangofactory.documentation.swagger.dto.AuthorizationType;
import com.mangofactory.documentation.swagger.dto.BasicAuth;
import com.mangofactory.documentation.swagger.dto.GrantType;
import com.mangofactory.documentation.swagger.dto.ImplicitGrant;
import com.mangofactory.documentation.swagger.dto.LoginEndpoint;
import com.mangofactory.documentation.swagger.dto.OAuth;
import com.mangofactory.documentation.swagger.dto.TokenEndpoint;
import com.mangofactory.documentation.swagger.dto.TokenRequestEndpoint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract OAuth toSwaggerOAuth(com.mangofactory.documentation.service.OAuth from);
  public abstract BasicAuth toSwaggerBasicAuth(com.mangofactory.documentation.service.BasicAuth from);
  public abstract ApiKey toSwaggerApiKey(com.mangofactory.documentation.service.ApiKey from);
  public abstract ImplicitGrant toSwaggerImplicitGrant(com.mangofactory.documentation.service.ImplicitGrant from);
  public abstract AuthorizationCodeGrant
    toSwaggerAuthorizationCodeGrant(com.mangofactory.documentation.service.AuthorizationCodeGrant from);
  public abstract TokenEndpoint toSwaggerTokenEndpoint(com.mangofactory.documentation.service.TokenEndpoint from);
  public abstract TokenRequestEndpoint
    toSwaggerTokenRequestEndpoint(com.mangofactory.documentation.service.TokenRequestEndpoint from);
  public abstract AuthorizationScope
    toSwaggerAuthorizationScope(com.mangofactory.documentation.service.AuthorizationScope from);
  public abstract Authorization toSwaggerAuthorization(com.mangofactory.documentation.service.Authorization from);
  public abstract LoginEndpoint toSwaggerLoginEndpoint(com.mangofactory.documentation.service.LoginEndpoint from);

  public com.mangofactory.documentation.swagger.dto.GrantType toSwaggerGrantType(
          com.mangofactory.documentation.service.GrantType from) {

    if (from instanceof com.mangofactory.documentation.service.ImplicitGrant) {
      return toSwaggerImplicitGrant((com.mangofactory.documentation.service.ImplicitGrant) from);
    } else if (from instanceof com.mangofactory.documentation.service.AuthorizationCodeGrant) {
      return toSwaggerAuthorizationCodeGrant(
              (com.mangofactory.documentation.service.AuthorizationCodeGrant) from);
    }
    throw new UnsupportedOperationException();
  }


  public com.mangofactory.documentation.swagger.dto.AuthorizationType toSwaggerAuthorizationType(
          com.mangofactory.documentation.service.AuthorizationType from) {

    if (from instanceof com.mangofactory.documentation.service.ApiKey) {
      return toSwaggerApiKey((com.mangofactory.documentation.service.ApiKey)from);
    } else if (from instanceof com.mangofactory.documentation.service.OAuth) {
      return toSwaggerOAuth((com.mangofactory.documentation.service.OAuth)from);
    } else if (from instanceof com.mangofactory.documentation.service.BasicAuth) {
      return toSwaggerBasicAuth((com.mangofactory.documentation.service.BasicAuth)from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<AuthorizationScope> toSwaggerAuthorizationScopes(
          List<com.mangofactory.documentation.service.AuthorizationScope> from);

  public abstract List<GrantType> toSwaggerGrantTypes(List<com.mangofactory.documentation.service.GrantType> from);

  public abstract List<AuthorizationType> toSwaggerAuthorizationTypes(
          List<com.mangofactory.documentation.service.AuthorizationType> from);

}
