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

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;

public abstract class SecurityScheme {
  protected final String name;
  protected final String type;
  private final List<VendorExtension> vendorExtensions = newArrayList();

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
    return ImmutableList.copyOf(vendorExtensions);
  }

  protected void addValidVendorExtensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(FluentIterable.from(nullToEmptyList(vendorExtensions))
        .filter(new Predicate<VendorExtension>() {
          @Override
          public boolean apply(VendorExtension input) {
            return input.getName().toLowerCase().startsWith("x-");
          }
        })
        .toList());
  }
}
