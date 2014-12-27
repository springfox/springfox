package com.mangofactory.service.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Lists.*;

public class ResourceListing {
  private final String apiVersion;
  private final String swaggerVersion;
  private final List<ApiListingReference> apis;
  private final LinkedHashMap<String, AuthorizationType> authorizations;
  private final ApiInfo info;

  public ResourceListing(String apiVersion,
      String swaggerVersion,
      List<ApiListingReference> apis,
      List <AuthorizationType> authorizations,
      ApiInfo info) {

    this.apiVersion = apiVersion;
    this.swaggerVersion = swaggerVersion;
    this.apis = apis;
    this.authorizations = initializeAuthTypes(authorizations);
    this.info = info;
  }

  private LinkedHashMap<String, AuthorizationType> initializeAuthTypes(List<AuthorizationType> authorizationTypes) {
    LinkedHashMap<String, AuthorizationType> mapped = new LinkedHashMap<String, AuthorizationType>();
    List<AuthorizationType> emptyList = newArrayList();
    for (AuthorizationType authorizationType : fromNullable(authorizationTypes).or(emptyList)) {
      mapped.put(authorizationType.getType(), authorizationType);
    }
    return mapped;
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
