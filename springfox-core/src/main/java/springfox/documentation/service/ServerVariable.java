/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
import java.util.StringJoiner;

public class ServerVariable {
  private final String name;
  private final List<String> allowedValues = new ArrayList<>();
  private final String defaultValue;
  private final String description;
  private final List<VendorExtension> extensions = new ArrayList<>();

  public ServerVariable(
      String name,
      List<String> allowedValues,
      String defaultValue,
      String description,
      List<VendorExtension> extensions) {
    this.defaultValue = defaultValue;
    this.description = description;
    this.name = name;
    this.allowedValues.addAll(allowedValues);
    this.extensions.addAll(extensions);
  }

  public List<String> getAllowedValues() {
    return allowedValues;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServerVariable that = (ServerVariable) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(allowedValues, that.allowedValues) &&
        Objects.equals(defaultValue, that.defaultValue) &&
        Objects.equals(description, that.description) &&
        Objects.equals(extensions, that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, allowedValues, defaultValue, description, extensions);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ServerVariable.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("allowedValues=" + allowedValues)
        .add("defaultValue='" + defaultValue + "'")
        .add("description='" + description + "'")
        .add("extensions=" + extensions)
        .toString();
  }
}

