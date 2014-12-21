package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.AuthorizationScope;
import com.mangofactory.service.model.OAuth;
import com.mangofactory.service.model.GrantType;

import java.util.ArrayList;
import java.util.List;

public class OAuthBuilder {

  private List<AuthorizationScope> scopes = new ArrayList<AuthorizationScope>();
  private List<GrantType> grantTypes = new ArrayList<GrantType>();


  public OAuthBuilder scopes(List<AuthorizationScope> scopes) {
    this.scopes.addAll(scopes);
    return this;
  }

  public OAuthBuilder grantTypes(List<GrantType> grantTypes) {
    this.grantTypes.addAll(grantTypes);
    return this;
  }

  public OAuth build() {
    return new OAuth(this.scopes, this.grantTypes);
  }
}
