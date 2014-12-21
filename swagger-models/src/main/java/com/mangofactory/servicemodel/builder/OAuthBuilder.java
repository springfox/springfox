package com.mangofactory.servicemodel.builder;

import com.mangofactory.servicemodel.AuthorizationScope;
import com.mangofactory.servicemodel.GrantType;
import com.mangofactory.servicemodel.OAuth;

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
