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

import com.google.common.base.Objects;
import org.springframework.core.Ordered;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

public class Tag implements Ordered {
  private final String name;
  private final String description;
  private final int order;

  public Tag(String name, String description) {
    this(name, description, Integer.MAX_VALUE);
  }

  public Tag(String name, String description, int order) {
    this.name = checkNotNull(emptyToNull(name));
    this.description = description;
    this.order = order;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equal(name, tag.name) &&
        Objects.equal(description, tag.description);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, description);
  }
}
