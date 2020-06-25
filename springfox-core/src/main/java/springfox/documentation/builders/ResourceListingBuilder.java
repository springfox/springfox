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

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ResourceListingBuilder {
  private String apiVersion;
  private final List<ApiListingReference> apis = new ArrayList<>();
  private final List<SecurityScheme> securitySchemes = new ArrayList<>();
  private ApiInfo info;
  private final List<Server> servers = new ArrayList<>();

  /**
   * Updates the api version
   *
   * @param apiVersion - api version
   * @return this
   */
  public ResourceListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = defaultIfAbsent(apiVersion, this.apiVersion);
    return this;
  }

  /**
   * Updates the api listed within this resource listing
   *
   * @param apis - apis
   * @return this
   */
  public ResourceListingBuilder apis(List<ApiListingReference> apis) {
    this.apis.addAll(nullToEmptyList(apis));
    return this;
  }

  /**
   * Updates the security definitions that protect this resource listing
   *
   * @param authorizations - security definitions
   * @return this
   */
  public ResourceListingBuilder securitySchemes(List<SecurityScheme> authorizations) {
    this.securitySchemes.addAll(nullToEmptyList(authorizations));
    return this;
  }

  /**
   * Updates the api information
   *
   * @param info - api info~
   * @return this
   */
  public ResourceListingBuilder info(ApiInfo info) {
    this.info = defaultIfAbsent(info, this.info);
    return this;
  }

  public ResourceListing build() {
    return new ResourceListing(apiVersion, apis, securitySchemes, info, servers);
  }

  public ResourceListingBuilder servers(Collection<Server> servers) {
    this.servers.addAll(nullToEmptyList(servers));
    return this;
  }
}