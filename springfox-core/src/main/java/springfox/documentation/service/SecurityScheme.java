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

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public abstract class SecurityScheme {
  protected final String name;
  protected final String type;
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  protected SecurityScheme(String name, String type) {
    this.type = type;
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public List<VendorExtension> getVendorExtensions() {
    return unmodifiableList(vendorExtensions);
  }

  protected void addValidVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(nullToEmptyList(vendorExtensions).stream()
        .filter(input -> input.getName().toLowerCase().startsWith("x-"))
        .collect(toList()));
  }
}
