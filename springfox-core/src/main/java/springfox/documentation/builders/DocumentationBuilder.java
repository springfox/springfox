/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.builders;

import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.Tag;

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
