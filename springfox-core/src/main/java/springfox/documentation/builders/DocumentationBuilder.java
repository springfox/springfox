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

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.Tag;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class DocumentationBuilder {
  private String groupName;
  private Multimap<String, ApiListing> apiListings = TreeMultimap.create(Ordering.natural(), byListingPosition());
  private ResourceListing resourceListing;
  private Set<Tag> tags = newHashSet();
  private String basePath;
  private Set<String> produces = newHashSet();
  private Set<String> consumes = newHashSet();
  private String host;
  private Set<String> schemes = newHashSet();
  private List<VendorExtension> vendorExtensions = new ArrayList<VendorExtension>();


  /**
   * Name of the documentation group
   *
   * @param groupName - group name
   * @return
   */
  public DocumentationBuilder name(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  /**
   * Updates the map with new entries
   *
   * @param apiListings - entries to add to the existing documentation
   * @return this
   */
  public DocumentationBuilder apiListingsByResourceGroupName(Multimap<String, ApiListing> apiListings) {
    this.apiListings.putAll(nullToEmptyMultimap(apiListings));
    return this;
  }

  /**
   * Updates the resource listing
   *
   * @param resourceListing - resource listing
   * @return this
   */
  public DocumentationBuilder resourceListing(ResourceListing resourceListing) {
    this.resourceListing = defaultIfAbsent(resourceListing, this.resourceListing);
    return this;
  }

  /**
   * Updates the tags with new entries
   *
   * @param tags - new tags
   * @return this
   */
  public DocumentationBuilder tags(Set<Tag> tags) {
    this.tags.addAll(nullToEmptySet(tags));
    return this;
  }

  /**
   * Updates the existing media types with new entries that this documentation produces
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public DocumentationBuilder produces(Set<String> mediaTypes) {
    this.produces.addAll(nullToEmptySet(mediaTypes));
    return this;
  }

  /**
   * Updates the existing media types with new entries that this documentation consumes
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public DocumentationBuilder consumes(Set<String> mediaTypes) {
    this.consumes.addAll(nullToEmptySet(mediaTypes));
    return this;
  }

  /**
   * Updates the host (name or ip) serving this api.
   *
   * @param host - new host
   * @return this
   */
  public DocumentationBuilder host(String host) {
    this.host = defaultIfAbsent(host, this.host);
    return this;
  }

  /**
   * Updates the schemes this api supports
   *
   * @param schemes - new schemes
   * @return this
   */
  public DocumentationBuilder schemes(Set<String> schemes) {
    this.schemes.addAll(nullToEmptySet(schemes));
    return this;
  }

  /**
   * Base path for this API
   *
   * @param basePath - base path
   * @return this
   */
  public DocumentationBuilder basePath(String basePath) {
    this.basePath = defaultIfAbsent(basePath, this.basePath);
    return this;
  }

  /**
   * Adds extensions for this API
   *
   * @param extensions - extensions
   * @return this
   */
  public DocumentationBuilder extensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }


  public static Comparator<ApiListing> byListingPosition() {
    return new Comparator<ApiListing>() {
      @Override
      public int compare(ApiListing first, ApiListing second) {
        return first.getPosition() - second.getPosition();
      }
    };
  }

  public Documentation build() {
    return new Documentation(
        groupName,
        basePath,
        tags,
        apiListings,
        resourceListing,
        produces,
        consumes,
        host,
        schemes,
        vendorExtensions);
  }
}
