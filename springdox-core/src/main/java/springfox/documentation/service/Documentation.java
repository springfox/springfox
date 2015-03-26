package springfox.documentation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;

public class Documentation {
  private final String groupName;
  private final String basePath;
  private final Map<String, ApiListing> apiListings;
  private final Set<Tag> tags;
  private final ResourceListing resourceListing;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> schemes;

  public Documentation(String groupName,
                       String basePath, Set<Tag> tags,
                       Map<String, ApiListing> apiListings,
                       ResourceListing resourceListing, Set<String> produces, Set<String> consumes, Set<String> schemes) {
    this.groupName = groupName;
    this.basePath = basePath;
    this.tags = tags;
    this.apiListings = apiListings;
    this.resourceListing = resourceListing;
    this.produces = produces;
    this.consumes = consumes;
    this.schemes = schemes;
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

  public String getBasePath() {
    return basePath;
  }

  public List<String> getProduces() {
    return newArrayList(produces);
  }

  public List<String> getSchemes() {
    return newArrayList(schemes);
  }

  public List<String> getConsumes() {
    return newArrayList(consumes);
  }
}
