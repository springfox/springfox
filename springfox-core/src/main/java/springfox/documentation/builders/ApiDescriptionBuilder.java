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

package springfox.documentation.builders;


import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class ApiDescriptionBuilder {
  private String groupName;
  private String path;
  private String summary;
  private String description;
  private List<Operation> operations;
  private Boolean hidden;
  private Function<String, String> pathDecorator = identity();
  private final Comparator<Operation> operationOrdering;

  public ApiDescriptionBuilder(Comparator<Operation> operationOrdering) {
    this.operationOrdering = operationOrdering;
  }

  /**
   * Updates the path to the api operation
   *
   * @param path - operation path
   * @return this @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder path(String path) {
    this.path = defaultIfAbsent(path, this.path);
    return this;
  }

  /**
   * Updates the descriptions to the api operation
   *
   * @param description - operation description
   * @return this @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  /**
   * Updates the operations to the api operation
   *
   * @param operations - operations for each of the http methods for that path
   * @return this @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder operations(List<Operation> operations) {
    if (operations != null) {
      this.operations = operations.stream().sorted(operationOrdering).collect(toList());
    }
    return this;
  }

  /**
   * Marks the operation as hidden
   *
   * @param hidden - operation path
   * @return this @see springfox.documentation.builders.ApiDescriptionBuilder
   */
  public ApiDescriptionBuilder hidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  public ApiDescriptionBuilder pathDecorator(Function<String, String> pathDecorator) {
    this.pathDecorator = defaultIfAbsent(pathDecorator, this.pathDecorator);
    return this;
  }

  /**
   * Updates the group name the api operation belongs to
   *
   * @param groupName -  group this api description belongs to
   * @return this @see springfox.documentation.builders.ApiDescriptionBuilder
   * @since 2.8.1
   */
  public ApiDescriptionBuilder groupName(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  public ApiDescriptionBuilder summary(String summary) {
    this.summary = summary;
    return this;
  }

  public ApiDescription build() {
    return new ApiDescription(
        groupName,
        pathDecorator.apply(path),
        summary,
        description,
        operations,
        hidden);
  }
}