/*
 *
 *  Copyright 2015-2019 the original author or authors.
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


import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.service.Tags.*;

@SuppressWarnings("deprecation")
public class ApiListingBuilder {
  private final Comparator<ApiDescription> descriptionOrdering;
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private String description;
  private String host;
  private int position;

  private Set<String> produces = new TreeSet<>();
  private Set<String> consumes = new TreeSet<>();
  private final Set<String> protocol = new TreeSet<>();
  private List<SecurityReference> securityReferences = new ArrayList<>();
  private List<ApiDescription> apis = new ArrayList<>();

  private final Set<Tag> tags = new TreeSet<>(tagComparator());
  private final Set<String> tagNames = new TreeSet<>();
  private final Map<String, springfox.documentation.schema.Model> models = new TreeMap<>();
  private final Map<String, Tag> tagLookup = new TreeMap<>();
  private final Map<String, ModelSpecification> modelSpecifications = new TreeMap<>();
  private ModelNamesRegistry modelNamesRegistry;

  /**
   * Update the sorting order for api descriptions
   *
   * @param descriptionOrdering - ordering for the api descriptions
   */
  public ApiListingBuilder(Comparator<ApiDescription> descriptionOrdering) {
    this.descriptionOrdering = descriptionOrdering;
  }

  /**
   * Updates the api version
   *
   * @param apiVersion - api version
   * @return this
   */
  public ApiListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = defaultIfAbsent(
        apiVersion,
        this.apiVersion);
    return this;
  }

  /**
   * Updates base path for the api listing
   *
   * @param basePath - base path
   * @return this
   */
  public ApiListingBuilder basePath(String basePath) {
    this.basePath = defaultIfAbsent(
        basePath,
        this.basePath);
    return this;
  }

  /**
   * Updates resource path for the api listing
   *
   * @param resourcePath - resource path
   * @return this
   */
  public ApiListingBuilder resourcePath(String resourcePath) {
    this.resourcePath = defaultIfAbsent(
        resourcePath,
        this.resourcePath);
    return this;
  }

  /**
   * Replaces the existing media types with new entries that this documentation produces
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public ApiListingBuilder produces(Set<String> mediaTypes) {
    if (mediaTypes != null) {
      this.produces = new HashSet<>(mediaTypes);
    }
    return this;
  }

  /**
   * Replaces the existing media types with new entries that this documentation consumes
   *
   * @param mediaTypes - new media types
   * @return this
   */
  public ApiListingBuilder consumes(Set<String> mediaTypes) {
    if (mediaTypes != null) {
      this.consumes = new HashSet<>(mediaTypes);
    }
    return this;
  }

  /**
   * Appends to the exiting collection of supported media types this listing produces
   *
   * @param produces - new media types
   * @return this
   */
  public ApiListingBuilder appendProduces(List<String> produces) {
    this.produces.addAll(nullToEmptyList(produces).stream()
        .filter(Objects::nonNull)
        .collect(toSet()));
    return this;
  }

  /**
   * Appends to the exiting collection of supported media types this listing consumes
   *
   * @param consumes - new media types
   * @return this
   */
  public ApiListingBuilder appendConsumes(List<String> consumes) {
    this.consumes.addAll(nullToEmptyList(consumes).stream()
        .filter(Objects::nonNull)
        .collect(toSet()));
    return this;
  }


  /**
   * Updates the host
   *
   * @param host - new host
   * @return this
   */
  public ApiListingBuilder host(String host) {
    this.host = defaultIfAbsent(
        host,
        this.host);
    return this;
  }


  /**
   * Appends to the exiting collection of supported protocols
   *
   * @param protocols - new protocols
   * @return this
   */
  public ApiListingBuilder protocols(Set<String> protocols) {
    this.protocol.addAll(nullToEmptySet(protocols));
    return this;
  }

  /**
   * Updates the references to the security definitions
   *
   * @param securityReferences - security definition references
   * @return this
   */
  public ApiListingBuilder securityReferences(List<SecurityReference> securityReferences) {
    if (securityReferences != null) {
      this.securityReferences = new ArrayList<>(securityReferences);
    }
    return this;
  }

  /**
   * Updates the apis
   *
   * @param apis - apis
   * @return this
   */
  public ApiListingBuilder apis(List<ApiDescription> apis) {
    if (apis != null) {
      this.apis = apis.stream().sorted(descriptionOrdering).collect(toList());
    }
    return this;
  }

  /**
   * Adds to the models collection
   *
   * @param models - model entries by name
   * @return this
   */
  public ApiListingBuilder models(Map<String, springfox.documentation.schema.Model> models) {
    this.models.putAll(nullToEmptyMap(models));
    return this;
  }

  /**
   * Adds to the models collection
   *
   * @param models - model entries by name
   * @return this
   */
  public ApiListingBuilder modelSpecifications(Map<String, ModelSpecification> models) {
    this.modelSpecifications.putAll(nullToEmptyMap(models));
    return this;
  }

  /**
   * Updates the description
   *
   * @param description - description of the api listing
   * @return this
   */
  public ApiListingBuilder description(String description) {
    this.description = defaultIfAbsent(
        description,
        this.description);
    return this;
  }

  /**
   * Updates the position of the listing
   *
   * @param position - position used to for sorting the listings
   * @return this
   */
  public ApiListingBuilder position(int position) {
    this.position = position;
    return this;
  }

  /**
   * Updates the tags
   *
   * @param tagNames - just the tag names
   * @return this
   */
  public ApiListingBuilder tagNames(Set<String> tagNames) {
    this.tagNames.addAll(nullToEmptySet(tagNames).stream()
        .filter(Objects::nonNull)
        .collect(toSet()));
    return this;
  }

  /**
   * Updates the tags.
   *
   * @param tags - Tag with name and description
   * @return - this
   * BREAKING Change in 2.4.0
   */
  public ApiListingBuilder tags(Set<Tag> tags) {
    this.tags.addAll(nullToEmptySet(tags));
    return this;
  }

  /**
   * Globally configured tags
   *
   * @param availableTags - tags available for services and operations
   * @return this
   */
  public ApiListingBuilder availableTags(Set<Tag> availableTags) {
    this.tagLookup.putAll(nullToEmptySet(availableTags).stream()
        .collect(toMap(
            Tag::getName,
            identity())));
    return this;
  }

  public ApiListingBuilder modelNamesRegistry(ModelNamesRegistry modelNamesRegistry) {
    this.modelNamesRegistry = modelNamesRegistry;
    return this;
  }

  public ApiListing build() {
    this.tags.addAll(tagNames.stream()
        .filter(emptyTags())
        .map(toTag(descriptor(
            tagLookup,
            description)))
        .collect(toSet()));
    return new ApiListing(
        apiVersion,
        basePath,
        resourcePath,
        produces,
        consumes,
        host,
        protocol,
        securityReferences,
        apis,
        models,
        modelSpecifications,
        modelNamesRegistry,
        description,
        position,
        tags);
  }
}