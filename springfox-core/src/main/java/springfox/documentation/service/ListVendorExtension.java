/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

import static java.util.Collections.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class ListVendorExtension<T> implements VendorExtension<List<T>> {
  private final List<T> values = new ArrayList<>();
  private final String name;

  public ListVendorExtension(
      String name,
      List<T> values) {
    this.name = name;
    this.values.addAll(nullToEmptyList(values));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<T> getValue() {
    return unmodifiableList(values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        values,
        name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ListVendorExtension<?> that = (ListVendorExtension<?>) o;

    return Objects.equals(
        values,
        that.values) &&
        Objects.equals(
            name,
            that.name);
  }
}
