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
package springfox.documentation.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static java.util.Collections.*;

public class ObjectVendorExtension implements VendorExtension<List<VendorExtension>> {
  private final List<VendorExtension> properties = new ArrayList<>();
  private final String name;

  public ObjectVendorExtension(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public List<VendorExtension> getValue() {
    return unmodifiableList(properties);
  }


  public void addProperty(VendorExtension property) {
    properties.add(property);
  }

  public void replaceProperty(VendorExtension property) {
    Optional<VendorExtension> vendorProperty =
        StreamSupport.stream(properties.spliterator(), false)
            .filter(withName(property.getName())).findFirst();

    vendorProperty.ifPresent(properties::remove);
    properties.add(property);
  }

  private Predicate<VendorExtension> withName(final String name) {
    return input -> input.getName().equals(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectVendorExtension that = (ObjectVendorExtension) o;
    return Objects.equals(properties, that.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(properties);
  }

  @Override
  public String toString() {
    return new StringBuilder(this.getClass().getSimpleName())
        .append("{")
        .append("properties=").append(properties).append(", ")
        .append("name=").append(name)
        .append("}").toString();
  }
}
