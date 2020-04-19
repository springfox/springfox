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

import java.util.List;
import java.util.Map;

public class Server {
  private String url = null;
  private String description = null;
  private Map<String, ServerVariable> variables = null;
  private List<VendorExtension> extensions = null;
  private final Map<String, ServerVariable> variables = new HashMap<>();

  public Server(
      String url,
      String description,
      Collection<ServerVariable> variables,
      List<VendorExtension> extensions) {
    this.url = url;
    this.description = description;
    this.variables = variables;
    this.extensions = extensions;
    variables.forEach(v -> this.variables.put(v.getName(), v));
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
}
