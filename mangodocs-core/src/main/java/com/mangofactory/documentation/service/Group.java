package com.mangofactory.documentation.service;

import java.util.Map;
import java.util.Set;

public class Group {
  private final String groupName;
  private final Map<String, ApiListing> apiListings;
  private final Set<Tag> tags;
  private final ResourceListing resourceListing;

  public Group(String groupName, Set<Tag> tags, Map<String, ApiListing> apiListings, ResourceListing resourceListing) {
    this.groupName = groupName;
    this.tags = tags;
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

  public Set<Tag> getTags() {
    return tags;
  }
}
