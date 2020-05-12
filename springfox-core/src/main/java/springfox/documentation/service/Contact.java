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

import java.util.List;

public class Contact {
  private final String name;
  private final String url;
  private final String email;
  private final List<VendorExtension> vendorExtensions;

  public Contact(String name, String url, String email, List<VendorExtension> vendorExtensions) {
    this.name = name;
    this.url = url;
    this.email = email;
    this.vendorExtensions = vendorExtensions;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getEmail() {
    return email;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }
}
