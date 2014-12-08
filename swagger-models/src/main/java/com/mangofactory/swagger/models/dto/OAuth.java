package com.mangofactory.swagger.models.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class OAuth extends AuthorizationType {

  private final List<AuthorizationScope> scopes;
  private final LinkedHashMap<String, GrantType> grantTypes;

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

  public List<GrantType> getGrantTypes() {
    return new ArrayList<GrantType>(grantTypes.values());
  }
}
