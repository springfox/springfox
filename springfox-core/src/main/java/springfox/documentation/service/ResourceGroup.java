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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.*;

public class ResourceGroup {
  private final String groupName;
  private final Class<?> controllerClazz;
  private final Integer position;

  public ResourceGroup(String groupName, Class<?> controllerClazz) {
    this(groupName, controllerClazz, 0);
  }

  public ResourceGroup(String groupName, Class<?> controllerClazz, Integer position) {
    this.groupName = groupName;
    this.controllerClazz = controllerClazz;
    this.position = position;
  }

  public String getGroupName() {
    return groupName;
  }

  public Integer getPosition() {
    return position;
  }

  public Optional<? extends Class<?>> getControllerClass() {
    return ofNullable(controllerClazz);
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    final ResourceGroup rhs = (ResourceGroup) obj;

    return Objects.equals(this.groupName, rhs.groupName)
        && Objects.equals(this.position, rhs.position)
        && Objects.equals(this.controllerClazz, rhs.controllerClazz);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, controllerClazz, position);
  }

  @Override
  public String toString() {
    return String.format(
        "ResourceGroup{groupName='%s', position=%d, controller=%s}",
        groupName,
        position,
        getControllerClass().map((Function<Class<?>, String>) Class::getName).orElse(""));
  }
}
