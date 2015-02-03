package com.mangofactory.documentation.service;

import java.util.Map;

public class Group {
  private final String groupName;
  private final Map<String, ApiListing> apiListings;
  private final ResourceListing resourceListing;

  public Group(String groupName, Map<String, ApiListing> apiListings, ResourceListing resourceListing) {
    this.groupName = groupName;
    this.apiListings = apiListings;
    this.resourceListing = resourceListing;
  }

  public String getGroupName() {
    return groupName;
  }

  public Map<String, ApiListing> getApiListings() {
    return apiListings;
  }

  public ResourceListing getResourceListing() {
    return resourceListing;
  }
}
