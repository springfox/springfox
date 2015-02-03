package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.model.AuthorizationScope;
import com.mangofactory.documentation.service.model.GrantType;
import com.mangofactory.documentation.service.model.OAuth;

import java.util.ArrayList;
import java.util.List;

public class OAuthBuilder {

  private List<AuthorizationScope> scopes = new ArrayList<AuthorizationScope>();
  private List<GrantType> grantTypes = new ArrayList<GrantType>();


  public OAuthBuilder scopes(List<AuthorizationScope> scopes) {
    if (scopes != null) {
      this.scopes.addAll(scopes);
    }
    return this;
  }

  public OAuthBuilder grantTypes(List<GrantType> grantTypes) {
    if (grantTypes != null) {
      this.grantTypes.addAll(grantTypes);
    }
    return this;
  }

  public OAuth build() {
    return new OAuth(this.scopes, this.grantTypes);
  }
}
