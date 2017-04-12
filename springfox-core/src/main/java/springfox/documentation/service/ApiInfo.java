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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ApiInfo {

  public static final Contact DEFAULT_CONTACT = new Contact("", "", "");
  public static final ApiInfo DEFAULT = new ApiInfo("Api Documentation", "Api Documentation", "1.0", "urn:tos",
          DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<VendorExtension>());

  private final String version;
  private final String title;
  private final String description;
  private final String termsOfServiceUrl;
  private final String license;
  private final String licenseUrl;
  private final Contact contact;
  private final List<VendorExtension> vendorExtensions;

  /**
   * Deprecated in favor of richer contact object
   * @deprecated @since 2.4.0
   */
  @Deprecated
  public ApiInfo(
      String title,
      String description,
      String version,
      String termsOfServiceUrl,
      String contactName,
      String license,
      String licenseUrl) {
    this(title, description, version, termsOfServiceUrl, new Contact(contactName, "", ""), license, licenseUrl, new ArrayList<VendorExtension>());
  }

  public ApiInfo(
      String title,
      String description,
      String version,
      String termsOfServiceUrl,
      Contact contact,
      String license,
      String licenseUrl,
      Collection<VendorExtension> vendorExtensions) {
    this.title = title;
    this.description = description;
    this.version = version;
    this.termsOfServiceUrl = termsOfServiceUrl;
    this.contact = contact;
    this.license = license;
    this.licenseUrl = licenseUrl;
    this.vendorExtensions = newArrayList(vendorExtensions);
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getTermsOfServiceUrl() {
    return termsOfServiceUrl;
  }

  public Contact getContact() {
    return contact;
  }

  public String getLicense() {
    return license;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public String getVersion() {
    return version;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }
}
