/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.spi.schema.contexts;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Objects;

public class ResolvedJsonViewType {
  private ResolvedType type;
  private JsonView view;

  public ResolvedJsonViewType(ResolvedType type, JsonView view) {
    this.type = type;
    this.view = view;
  }

  public ResolvedType getType() {
    return type;
  }

  public void setType(ResolvedType type) {
    this.type = type;
  }

  public JsonView getView() {
    return view;
  }

  public void setView(JsonView view) {
    this.view = view;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResolvedJsonViewType that = (ResolvedJsonViewType) o;
    return Objects.equal(type, that.type) &&
        Objects.equal(view, that.view);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, view);
  }
}
