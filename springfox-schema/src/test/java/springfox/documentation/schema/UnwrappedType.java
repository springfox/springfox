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

package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class UnwrappedType {
  @JsonUnwrapped
  private Category category;

  @JsonUnwrapped
  public Category getCategory() {
    return category;
  }

  @JsonUnwrapped
  public void setCategory(Category category) {
    this.category = category;
  }
}

class UnwrappedTypeForField {
  @JsonUnwrapped
  private Category category;
}

class UnwrappedTypeForGetter {
  private Category category;

  @JsonUnwrapped
  public Category getCategory() {
    return category;
  }

}

class UnwrappedTypeForSetter {
  private Category category;

  public Category getCategory() {
    return category;
  }

  @JsonUnwrapped
  public void setCategory(Category category) {
    this.category = category;
  }
}

class UnwrappedTypeForFieldWithGetter {
  @JsonUnwrapped
  private Category category;

  public Category getCategory() {
    return category;
  }
}