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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.Optional.*;

public class ApiDescription {
  private final String groupName;
  private final String path;
  private final String summary;
  private final String description;
  private final List<Operation> operations;
  private final Boolean hidden;

  public ApiDescription(
      String groupName,
      String path,
      String summary,
      String description,
      List<Operation> operations,
      Boolean hidden) {
    this.groupName = groupName;
    this.path = path;
    this.summary = summary;
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
    return ofNullable(groupName);
  }

  public String getSummary() {
    return summary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiDescription that = (ApiDescription) o;
    return Objects.equals(groupName, that.groupName) &&
        Objects.equals(path, that.path) &&
        Objects.equals(summary, that.summary) &&
        Objects.equals(description, that.description) &&
        Objects.equals(operations, that.operations) &&
        Objects.equals(hidden, that.hidden);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, path, summary, description, operations, hidden);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ApiDescription.class.getSimpleName() + "[", "]")
        .add("groupName='" + groupName + "'")
        .add("path='" + path + "'")
        .add("summary='" + summary + "'")
        .add("description='" + description + "'")
        .add("operations=" + operations)
        .add("hidden=" + hidden)
        .toString();
  }
}
