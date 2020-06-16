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
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;

/**
 * Builds the api information
 */
public class ApiInfoBuilder {
  private String title;
  private String description;
  private String termsOfServiceUrl;
  private Contact contact;
  private String license;
  private String licenseUrl;
  private String version;
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  /**
   * Updates the api title
   *
   * @param title - title for the API
   * @return this
   */
  public ApiInfoBuilder title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Updates the api description
   *
   * @param description - api description
   * @return this
   */
  public ApiInfoBuilder description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Updates the terms of service url
   *
   * @param termsOfServiceUrl - url to the terms of service
   * @return this
   */
  public ApiInfoBuilder termsOfServiceUrl(String termsOfServiceUrl) {
    this.termsOfServiceUrl = termsOfServiceUrl;
    return this;
  }

  /**
   * Updates the api version
   *
   * @param version - of the API
   * @return this
   */
  public ApiInfoBuilder version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Updates contact information for the person responsible for this API
   *
   * @param contact - contact information
   * @return this
   */
  public ApiInfoBuilder contact(Contact contact) {
    this.contact = contact;
    return this;
  }

  /**
   * Updates license information for this API
   *
   * @param license licence string
   * @return this
   */
  public ApiInfoBuilder license(String license) {
    this.license = license;
    return this;
  }

  /**
   * Updates the license Url for this API
   *
   * @param licenseUrl - license Url
   * @return this
   */
  public ApiInfoBuilder licenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
    return this;
  }

  /**
   * Adds extensions for this API
   *
   * @param extensions -  extensions
   * @return this
   */
  public ApiInfoBuilder extensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  public ApiInfo build() {
    return new ApiInfo(title, description, version, termsOfServiceUrl, contact, license, licenseUrl, vendorExtensions);
  }
}