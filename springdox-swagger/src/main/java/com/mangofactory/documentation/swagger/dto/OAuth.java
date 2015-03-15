package com.mangofactory.documentation.swagger.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class OAuth extends AuthorizationType {

  private List<AuthorizationScope> scopes;
  private LinkedHashMap<String, GrantType> grantTypes;

  public OAuth() {
    super("oauth2");
  }

  public OAuth(List<AuthorizationScope> scopes, List<GrantType> gTypes) {
    super("oauth2");
    this.scopes = scopes;
    this.grantTypes = initializeGrantTypes(gTypes);

  }

  private LinkedHashMap<String, GrantType> initializeGrantTypes(List<GrantType> gTypes) {
    if (null != gTypes) {
      LinkedHashMap<String, GrantType> map = new LinkedHashMap<String, GrantType>();
      for (GrantType grantType : gTypes) {
        map.put(grantType.getType(), grantType);
      }
      return map;
    }
    return null;
  }

  @Override
  public String getName() {
    return super.type;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public void setScopes(List<AuthorizationScope> scopes) {
    this.scopes = scopes;
  }

  public List<GrantType> getGrantTypes() {
    return new ArrayList<GrantType>(grantTypes.values());
  }

  public void setGrantTypes(List<GrantType> grantTypes) {
    this.grantTypes = initializeGrantTypes(grantTypes);
  }
}
