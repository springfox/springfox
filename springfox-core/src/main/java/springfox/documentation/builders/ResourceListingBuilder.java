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

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.AuthorizationType;
import springfox.documentation.service.ResourceListing;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class ResourceListingBuilder {
  private String apiVersion;
  private List<ApiListingReference> apis = newArrayList();
  private List<AuthorizationType> authorizations = newArrayList();
  private ApiInfo info;

  public ResourceListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = defaultIfAbsent(apiVersion, this.apiVersion);
    return this;
  }

  public ResourceListingBuilder apis(List<ApiListingReference> apis) {
    this.apis.addAll(nullToEmptyList(apis));
    return this;
  }

  public ResourceListingBuilder authorizations(List<AuthorizationType> authorizations) {
    this.authorizations.addAll(nullToEmptyList(authorizations));
    return this;
  }

  public ResourceListingBuilder info(ApiInfo info) {
    this.info = defaultIfAbsent(info, this.info);
    return this;
  }

  public ResourceListing build() {
    return new ResourceListing(apiVersion, apis, authorizations, info);
  }
}