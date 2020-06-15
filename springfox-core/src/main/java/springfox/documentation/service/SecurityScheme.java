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
  private final String name;
  private final String type;
  private final String description;
  private final List<VendorExtension> extensions = new ArrayList<>();

  protected SecurityScheme(
      String name,
      String type) {
    this(name, type, "", new ArrayList<>());
  }

  public SecurityScheme(
      String name,
      String type,
      String description,
      List<VendorExtension> extensions) {
    this.name = name;
    this.type = type;
    this.description = description;
    this.extensions.addAll(extensions);
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<VendorExtension> getVendorExtensions() {
    return unmodifiableList(extensions);
  }

  protected void addValidVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.extensions.addAll(
        nullToEmptyList(vendorExtensions).stream()
                                         .filter(input -> input.getName().toLowerCase()
                                                               .startsWith("x-"))
                                         .collect(toList()));
  }
}
