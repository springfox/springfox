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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class Server {
  private final String name;
  private final String url;
  private final String description;
  private final Map<String, ServerVariable> variables = new HashMap<>();
  private final List<VendorExtension> extensions = new ArrayList<>();

  public Server(
      String name,
      String url,
      String description,
      Collection<ServerVariable> variables,
      List<VendorExtension> extensions) {
    this.name = name;
    this.url = url;
    this.description = description;
    variables.forEach(v -> this.variables.put(v.getName(), v));
    this.extensions.addAll(extensions);
  }

  public String getUrl() {
    return url;
  }

  public String getDescription() {
    return description;
  }

  public Collection<ServerVariable> getVariables() {
    return variables.values();
  }

  public Collection<VendorExtension> getExtensions() {
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
    Server server = (Server) o;
    return Objects.equals(name, server.name) &&
        Objects.equals(url, server.url) &&
        Objects.equals(description, server.description) &&
        Objects.equals(variables, server.variables) &&
        Objects.equals(extensions, server.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, url, description, variables, extensions);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Server.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("url='" + url + "'")
        .add("description='" + description + "'")
        .add("variables=" + variables)
        .add("extensions=" + extensions)
        .toString();
  }
}
