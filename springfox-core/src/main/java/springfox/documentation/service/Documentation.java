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

package springfox.documentation.service;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

public class Documentation {
  private final String groupName;
  private final String basePath;
  private final Multimap<String, ApiListing> apiListings;
  private final Set<Tag> tags;
  private final ResourceListing resourceListing;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final String host;
  private final Set<String> schemes;
  private final List<VendorExtension> vendorExtensions;

  public Documentation(
      String groupName,
      String basePath,
      Set<Tag> tags,
      Multimap<String, ApiListing> apiListings,
      ResourceListing resourceListing,
      Set<String> produces,
      Set<String> consumes,
      String host,
      Set<String> schemes,
      Collection<VendorExtension> vendorExtensions) {

    this.groupName = groupName;
    this.basePath = basePath;
    this.tags = tags;
    this.apiListings = apiListings;
    this.resourceListing = resourceListing;
    this.produces = produces;
    this.consumes = consumes;
    this.host = host;
    this.schemes = schemes;
    this.vendorExtensions = newArrayList(vendorExtensions);
  }

  public String getGroupName() {
    return groupName;
  }

  public Multimap<String, ApiListing> getApiListings() {
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

  public String getHost() {
    return host;
  }

  public List<String> getSchemes() {
    return newArrayList(schemes);
  }

  public List<String> getConsumes() {
    return newArrayList(consumes);
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }
}
