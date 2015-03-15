package com.mangofactory.documentation.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResourceListing {
  private final String apiVersion;
  private final List<ApiListingReference> apis;
  private final LinkedHashMap<String, AuthorizationType> authorizations;
  private final ApiInfo info;

  public ResourceListing(String apiVersion,
                         List<ApiListingReference> apis,
                         List<AuthorizationType> authorizations,
                         ApiInfo info) {

    this.apiVersion = apiVersion;
    this.apis = apis;
    this.authorizations = initializeAuthTypes(authorizations);
    this.info = info;
  }

  private LinkedHashMap<String, AuthorizationType> initializeAuthTypes(List<AuthorizationType> authorizationTypes) {
    LinkedHashMap<String, AuthorizationType> mapped = new LinkedHashMap<String, AuthorizationType>();
    List<AuthorizationType> emptyList = Lists.newArrayList();
    for (AuthorizationType authorizationType : Optional.fromNullable(authorizationTypes).or(emptyList)) {
      mapped.put(authorizationType.getType(), authorizationType);
    }
    return mapped;
  }

  public String getApiVersion() {
    return apiVersion;
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
