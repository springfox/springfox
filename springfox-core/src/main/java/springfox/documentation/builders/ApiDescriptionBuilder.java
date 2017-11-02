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

package springfox.documentation.builders;

import static springfox.documentation.builders.BuilderDefaults.defaultIfAbsent;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;

public class ApiDescriptionBuilder {
  private String path;
  private String description;
  private List<Operation> operations;
  private Comparator<Operation> operationOrdering;
  private Boolean hidden;
  private Function<String, String> pathDecorator = Function.identity();

  public ApiDescriptionBuilder(Comparator<Operation> operationOrdering) {
    this.operationOrdering = operationOrdering;
  }

  /**
   * Updates the path to the api operation
   *
   * @param path - operation path
   * @return @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder path(String path) {
    this.path = defaultIfAbsent(path, this.path);
    return this;
  }

  /**
   * Updates the descriptions to the api operation
   *
   * @param description - operation description
   * @return @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  /**
   * Updates the operations to the api operation
   *
   * @param operations - operations for each of the http methods for that path
   * @return @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder operations(List<Operation> operations) {
    if (operations != null) {
      this.operations = operations.stream()
                        .sorted(operationOrdering)
                        .collect(Collectors.toList());
    }
    return this;
  }

  /**
   * Marks the operation as hidden
   *
   * @param hidden - operation path
   * @return @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder hidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  public ApiDescriptionBuilder pathDecorator(Function<String, String> pathDecorator) {
    this.pathDecorator = defaultIfAbsent(pathDecorator, this.pathDecorator);
    return this;
  }

  public ApiDescription build() {
    return new ApiDescription(pathDecorator.apply(path), description, operations, hidden);
  }
}