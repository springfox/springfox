package com.mangofactory.swagger.models.dto;

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
