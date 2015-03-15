package springdox.documentation.builders;

import springdox.documentation.service.ApiListing;
import springdox.documentation.service.Documentation;
import springdox.documentation.service.ResourceListing;
import springdox.documentation.service.Tag;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

public class DocumentationBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings = newTreeMap();
  private ResourceListing resourceListing;
  private Set<Tag> tags = newHashSet();
  private String basePath;
  private Set<String> produces = newHashSet();
  private Set<String> consumes = newHashSet();
  private Set<String> schemes = newHashSet();

  public DocumentationBuilder name(String groupName) {
    this.groupName = BuilderDefaults.defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  public DocumentationBuilder apiListingsByResourceGroupName(Map<String, ApiListing> apiListings) {
    if (apiListings != null) {
      this.apiListings.putAll(apiListings);
    }
    return this;
  }

  public DocumentationBuilder resourceListing(ResourceListing resourceListing) {
    this.resourceListing = BuilderDefaults.defaultIfAbsent(resourceListing, this.resourceListing);
    return this;
  }
  
  public DocumentationBuilder tags(Set<Tag> tags) {
    this.tags.addAll(BuilderDefaults.nullToEmptySet(tags));
    return this;
  }

  public DocumentationBuilder produces(Set<String> mediaTypes) {
    this.produces.addAll(BuilderDefaults.nullToEmptySet(mediaTypes));
    return this;
  }

  public DocumentationBuilder consumes(Set<String> mediaTypes) {
    this.consumes.addAll(BuilderDefaults.nullToEmptySet(mediaTypes));
    return this;
  }

  public DocumentationBuilder schemes(Set<String> schemes) {
    this.schemes.addAll(BuilderDefaults.nullToEmptySet(schemes));
    return this;
  }

  public DocumentationBuilder basePath(String basePath) {
    this.basePath = BuilderDefaults.defaultIfAbsent(basePath, this.basePath);
    return this;
  }
  
  public Documentation build() {
    return new Documentation(groupName, basePath, tags, apiListings, resourceListing, produces, consumes, schemes);
  }
}
