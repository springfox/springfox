package com.mangofactory.swagger.models.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResourceListing {
  private final String apiVersion;
  private final String swaggerVersion;
  private final List<ApiListingReference> apis;
  private final LinkedHashMap<String, AuthorizationType> authorizations;
  private final ApiInfo info;

  public ResourceListing(String apiVersion, String swaggerVersion, List<ApiListingReference> apis, List
          <AuthorizationType> authorizations, ApiInfo info) {
    this.apiVersion = apiVersion;
    this.swaggerVersion = swaggerVersion;
    this.apis = apis;
    this.authorizations = initializeAuthTypes(authorizations);
    this.info = info;
  }

  private LinkedHashMap<String, AuthorizationType> initializeAuthTypes(List<AuthorizationType> authorizationTypes) {
    if (null != authorizationTypes) {
      LinkedHashMap<String, AuthorizationType> map = new LinkedHashMap<String, AuthorizationType>();
      for (AuthorizationType authorizationType : authorizationTypes) {
        map.put(authorizationType.getType(), authorizationType);
      }
      return map;
    }
    return null;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public String getSwaggerVersion() {
    return swaggerVersion;
  }

  public List<ApiListingReference> getApis() {
    return apis;
  }

  public List<AuthorizationType> getAuthorizations() {
    return new ArrayList<AuthorizationType>(authorizations.values());
  }

  public ApiInfo getInfo() {
    return info;
  }
}
