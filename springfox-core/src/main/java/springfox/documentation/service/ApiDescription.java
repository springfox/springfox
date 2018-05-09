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

import com.google.common.base.Optional;

import java.util.List;

public class ApiDescription {
  private final String groupName;
  private final String path;
  private final String description;
  private final List<Operation> operations;
  private final Boolean hidden;

  /**
   * For backwards compatibility. Please use the builder instead.
   * @see springfox.documentation.builders.ApiDescriptionBuilder
   * @deprecated @since 2.8.1
   * @param path
   * @param description
   * @param operations
   * @param hidden
   */
  @Deprecated
  public ApiDescription(
      String path,
      String description,
      List<Operation> operations,
      Boolean hidden) {
    this(null, path, description, operations, hidden);
  }

  public ApiDescription(
      String groupName,
      String path,
      String description,
      List<Operation> operations,
      Boolean hidden) {
    this.groupName = groupName;
    this.path = path;
    this.description = description;
    this.operations = operations;
    this.hidden = hidden;
  }

  public String getPath() {
    return path;
  }

  public String getDescription() {
    return description;
  }

  public List<Operation> getOperations() {
    return operations;
  }

  public Boolean isHidden() {
    return hidden;
  }

  public Optional<String> getGroupName() {
    return Optional.fromNullable(groupName);
  }
}
