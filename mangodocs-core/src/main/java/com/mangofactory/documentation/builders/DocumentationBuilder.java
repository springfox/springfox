package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.Documentation;
import com.mangofactory.documentation.service.ResourceListing;
import com.mangofactory.documentation.service.Tag;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class DocumentationBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings = newTreeMap();
  private ResourceListing resourceListing;
  private Set<Tag> tags = newHashSet();

  public DocumentationBuilder name(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  public DocumentationBuilder apiListingsByResourceGroupName(Map<String, ApiListing> apiListings) {
    if (apiListings != null) {
      this.apiListings.putAll(apiListings);
    }
    return this;
  }

  public DocumentationBuilder resourceListing(ResourceListing resourceListing) {
    this.resourceListing = defaultIfAbsent(resourceListing, this.resourceListing);
    return this;
  }
  
  public DocumentationBuilder tags(Set<Tag> tags) {
    this.tags.addAll(nullToEmptySet(tags));
    return this;
  }

  public Documentation build() {
    return new Documentation(groupName, tags, apiListings, resourceListing);
  }
}
