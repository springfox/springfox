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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class ObjectVendorExtension implements VendorExtension<List<VendorExtension>> {
  private final List<VendorExtension> properties = newArrayList();
  private final String name;

  public ObjectVendorExtension(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public List<VendorExtension> getValue() {
    return ImmutableList.copyOf(properties);
  }


  public void addProperty(VendorExtension property) {
    properties.add(property);
  }

  public void replaceProperty(VendorExtension property) {
    Optional<VendorExtension> vendorProperty = Iterables.tryFind(properties, withName(property.getName()));
    if (vendorProperty.isPresent()) {
      properties.remove(vendorProperty.get());
    }
    properties.add(property);
  }

  private Predicate<VendorExtension> withName(final String name) {
    return new Predicate<VendorExtension>() {
      @Override
      public boolean apply(VendorExtension input) {
        return input.getName().equals(name);
      }
    };
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
    return Objects.equal(properties, that.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(properties);
  }

  @Override
  public String toString() {
    return new StringBuffer(this.getClass().getSimpleName())
        .append("{")
        .append("properties=").append(properties).append(", ")
        .append("name=").append(name)
        .append("}").toString();
  }
}
