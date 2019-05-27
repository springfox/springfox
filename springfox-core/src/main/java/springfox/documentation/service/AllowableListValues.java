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

import java.util.List;
import java.util.Objects;

public class AllowableListValues implements AllowableValues {
  private final List<String> values;
  private final String valueType;

  public AllowableListValues(List<String> values, String valueType) {
    this.values = values;
    this.valueType = valueType;
  }

  public List<String> getValues() {
    return values;
  }

  public String getValueType() {
    return valueType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, valueType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AllowableListValues that = (AllowableListValues) o;

    return Objects.equals(values, that.values) &&
        Objects.equals(valueType, that.valueType);
  }
}
