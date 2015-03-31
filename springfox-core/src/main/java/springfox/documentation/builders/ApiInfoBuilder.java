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

public class ApiInfoBuilder {
  private String title;
  private String description;
  private String termsOfServiceUrl;
  private String contact;
  private String license;
  private String licenseUrl;
  private String version;

  public ApiInfoBuilder title(String title) {
    this.title = title;
    return this;
  }

  public ApiInfoBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ApiInfoBuilder termsOfServiceUrl(String termsOfServiceUrl) {
    this.termsOfServiceUrl = termsOfServiceUrl;
    return this;
  }

  public ApiInfoBuilder version(String version) {
    this.version = version;
    return this;
  }

  public ApiInfoBuilder contact(String contact) {
    this.contact = contact;
    return this;
  }

  public ApiInfoBuilder license(String license) {
    this.license = license;
    return this;
  }

  public ApiInfoBuilder licenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
    return this;
  }



  public ApiInfo build() {
    return new ApiInfo(title, description, version, termsOfServiceUrl, contact, license, licenseUrl);
  }
}