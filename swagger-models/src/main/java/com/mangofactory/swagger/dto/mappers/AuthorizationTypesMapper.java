package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.swagger.dto.ApiKey;
import com.mangofactory.swagger.dto.Authorization;
import com.mangofactory.swagger.dto.AuthorizationCodeGrant;
import com.mangofactory.swagger.dto.AuthorizationScope;
import com.mangofactory.swagger.dto.AuthorizationType;
import com.mangofactory.swagger.dto.BasicAuth;
import com.mangofactory.swagger.dto.ImplicitGrant;
import com.mangofactory.swagger.dto.LoginEndpoint;
import com.mangofactory.swagger.dto.OAuth;
import com.mangofactory.swagger.dto.TokenEndpoint;
import com.mangofactory.swagger.dto.TokenRequestEndpoint;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class AuthorizationTypesMapper {
  public abstract OAuth toSwagger(com.mangofactory.service.model.OAuth from);
  public abstract BasicAuth toSwagger(com.mangofactory.service.model.BasicAuth from);
  public abstract ApiKey toSwagger(com.mangofactory.service.model.ApiKey from);
  public abstract ImplicitGrant toSwagger(com.mangofactory.service.model.ImplicitGrant from);
  public abstract AuthorizationCodeGrant toSwagger(com.mangofactory.service.model.AuthorizationCodeGrant from);
  public abstract TokenEndpoint toSwagger(com.mangofactory.service.model.TokenEndpoint from);
  public abstract TokenRequestEndpoint toSwagger(com.mangofactory.service.model.TokenRequestEndpoint from);
  public abstract AuthorizationScope toSwagger(com.mangofactory.service.model.AuthorizationScope from);
  public abstract Authorization toSwagger(com.mangofactory.service.model.Authorization from);
  public abstract LoginEndpoint toSwagger(com.mangofactory.service.model.LoginEndpoint from);

  public com.mangofactory.swagger.dto.GrantType concreteToSwagger(com.mangofactory.service.model.GrantType from) {
    if (from instanceof com.mangofactory.service.model.ImplicitGrant) {
      return toSwagger((com.mangofactory.service.model.ImplicitGrant)from);
    } else if (from instanceof com.mangofactory.service.model.AuthorizationCodeGrant) {
      return toSwagger((com.mangofactory.service.model.AuthorizationCodeGrant)from);
    }
    throw new UnsupportedOperationException();
  }


  public com.mangofactory.swagger.dto.AuthorizationType concreteToSwagger(
          com.mangofactory.service.model.AuthorizationType from) {

    if (from instanceof com.mangofactory.service.model.ApiKey) {
      return toSwagger((com.mangofactory.service.model.ApiKey)from);
    } else if (from instanceof com.mangofactory.service.model.OAuth) {
      return toSwagger((com.mangofactory.service.model.OAuth)from);
    } else if (from instanceof com.mangofactory.service.model.BasicAuth) {
      return toSwagger((com.mangofactory.service.model.BasicAuth)from);
    }
    throw new UnsupportedOperationException();
  }

  //List types
  public abstract List<AuthorizationScope> authorizationScopesToSwagger(
          List<com.mangofactory.service.model.AuthorizationScope> from);

  public abstract List<AuthorizationType> toSwagger(List<com.mangofactory.service.model.AuthorizationType> from);

}
