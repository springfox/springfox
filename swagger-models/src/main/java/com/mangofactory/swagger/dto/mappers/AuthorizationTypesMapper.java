package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.swagger.dto.ApiKey;
import com.mangofactory.swagger.dto.Authorization;
import com.mangofactory.swagger.dto.AuthorizationCodeGrant;
import com.mangofactory.swagger.dto.AuthorizationScope;
import com.mangofactory.swagger.dto.AuthorizationType;
import com.mangofactory.swagger.dto.BasicAuth;
import com.mangofactory.swagger.dto.GrantType;
import com.mangofactory.swagger.dto.ImplicitGrant;
import com.mangofactory.swagger.dto.LoginEndpoint;
import com.mangofactory.swagger.dto.OAuth;
import com.mangofactory.swagger.dto.TokenEndpoint;
import com.mangofactory.swagger.dto.TokenRequestEndpoint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract OAuth toSwaggerOAuth(com.mangofactory.service.model.OAuth from);
  public abstract BasicAuth toSwaggerBasicAuth(com.mangofactory.service.model.BasicAuth from);
  public abstract ApiKey toSwaggerApiKey(com.mangofactory.service.model.ApiKey from);
  public abstract ImplicitGrant toSwaggerImplicitGrant(com.mangofactory.service.model.ImplicitGrant from);
  public abstract AuthorizationCodeGrant
    toSwaggerAuthorizationCodeGrant(com.mangofactory.service.model.AuthorizationCodeGrant from);
  public abstract TokenEndpoint toSwaggerTokenEndpoint(com.mangofactory.service.model.TokenEndpoint from);
  public abstract TokenRequestEndpoint
    toSwaggerTokenRequestEndpoint(com.mangofactory.service.model.TokenRequestEndpoint from);
  public abstract AuthorizationScope
    toSwaggerAuthorizationScope(com.mangofactory.service.model.AuthorizationScope from);
  public abstract Authorization toSwaggerAuthorization(com.mangofactory.service.model.Authorization from);
  public abstract LoginEndpoint toSwaggerLoginEndpoint(com.mangofactory.service.model.LoginEndpoint from);

  public com.mangofactory.swagger.dto.GrantType toSwaggerGrantType(com.mangofactory.service.model.GrantType from) {
    if (from instanceof com.mangofactory.service.model.ImplicitGrant) {
      return toSwaggerImplicitGrant((com.mangofactory.service.model.ImplicitGrant) from);
    } else if (from instanceof com.mangofactory.service.model.AuthorizationCodeGrant) {
      return toSwaggerAuthorizationCodeGrant((com.mangofactory.service.model.AuthorizationCodeGrant) from);
    }
    throw new UnsupportedOperationException();
  }


  public com.mangofactory.swagger.dto.AuthorizationType toSwaggerAuthorizationType(
          com.mangofactory.service.model.AuthorizationType from) {

    if (from instanceof com.mangofactory.service.model.ApiKey) {
      return toSwaggerApiKey((com.mangofactory.service.model.ApiKey)from);
    } else if (from instanceof com.mangofactory.service.model.OAuth) {
      return toSwaggerOAuth((com.mangofactory.service.model.OAuth)from);
    } else if (from instanceof com.mangofactory.service.model.BasicAuth) {
      return toSwaggerBasicAuth((com.mangofactory.service.model.BasicAuth)from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<AuthorizationScope> toSwaggerAuthorizationScopes(
          List<com.mangofactory.service.model.AuthorizationScope> from);

  public abstract List<GrantType> toSwaggerGrantTypes(List<com.mangofactory.service.model.GrantType> from);

  public abstract List<AuthorizationType> toSwaggerAuthorizationTypes(List<com.mangofactory.service.model
          .AuthorizationType> from);

}
