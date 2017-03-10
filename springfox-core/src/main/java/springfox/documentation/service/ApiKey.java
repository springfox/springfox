/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import java.util.List;

public class ApiKey extends SecurityScheme {
  private final String keyname;
  private final String passAs;

  public ApiKey(String name, String keyname, String passAs) {
    this(name, keyname, passAs, new ArrayList<VendorExtension>());
  }

  public ApiKey(String name, String keyname, String passAs, List<VendorExtension> vendorExtensions) {
    super(name, "apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
    addValidVendorExtensions(vendorExtensions);
  }

  public String getKeyname() {
    return keyname;
  }

  public String getPassAs() {
    return passAs;
  }
}
