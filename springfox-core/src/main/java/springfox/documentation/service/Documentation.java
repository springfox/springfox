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

package springfox.documentation.service;

import springfox.documentation.common.ExternalDocumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Documentation {
  private final String groupName;
  private final String basePath;
  private final Map<String, List<ApiListing>> apiListings;
  private final Set<Tag> tags;
  private final ResourceListing resourceListing;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final String host;
  private final Set<String> schemes;
  private final List<Server> servers = new ArrayList<>();
  private final ExternalDocumentation externalDocumentation;
  private final List<VendorExtension> vendorExtensions;

  @SuppressWarnings("ParameterNumber")
  public Documentation(
      String groupName,
      String basePath,
      Set<Tag> tags,
      Map<String, List<ApiListing>> apiListings,
      ResourceListing resourceListing,
      Set<String> produces,
      Set<String> consumes,
      String host,
      Set<String> schemes,
      Collection<Server> servers,
      ExternalDocumentation externalDocumentation,
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
    this.servers.addAll(servers);
    this.externalDocumentation = externalDocumentation;
    this.vendorExtensions = new ArrayList<>(vendorExtensions);
  }

  public String getGroupName() {
    return groupName;
  }

  public Map<String, List<ApiListing>> getApiListings() {
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
    return new ArrayList<>(produces);
  }

  public String getHost() {
    return host;
  }

  public List<String> getSchemes() {
    return new ArrayList<>(schemes);
  }

  public List<String> getConsumes() {
    return new ArrayList<>(consumes);
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public List<Server> getServers() {
    return servers;
  }

  public ExternalDocumentation getExternalDocumentation() {
    return externalDocumentation;
  }

  public void addServer(Server inferredServer) {
    if (!servers.contains(inferredServer)) {
      servers.add(inferredServer);
    }
  }
}
