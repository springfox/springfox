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
package springfox.documentation.spi.service.contexts;

import springfox.documentation.PathProvider;
import springfox.documentation.service.Operation;
import springfox.documentation.service.RequestParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public class PathContext {

  private final RequestMappingContext parent;
  private final Optional<Operation> operation;

  public PathContext(RequestMappingContext parent, Optional<Operation> operation) {
    this.parent = parent;
    this.operation = operation;
  }

  public DocumentationContext documentationContext() {
    return parent.getDocumentationContext();
  }

  public PathProvider pathProvider() {
    return parent.getDocumentationContext().getPathProvider();
  }

  @SuppressWarnings("deprecation")
  public List<springfox.documentation.service.Parameter> getParameters() {
    if (operation.isPresent()) {
      return operation.get().getParameters();
    }
    return new ArrayList<>();
  }

  public Collection<RequestParameter> getRequestParameters() {
    if (operation.isPresent()) {
      return operation.get().getRequestParameters();
    }
    return new HashSet<>();
  }
}
