/*
 *
 *  Copyright 2019 the original author or authors.
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

package springfox.documentation.spring.web.scanners;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ComparisonCondition {

  private final String modelFor;

  private final Set<String> modelsTo;

  private final Set<String> conditions;

  public ComparisonCondition(String modelFor, Set<String> modelsTo, Set<String> conditions) {
    this.modelFor = modelFor;
    this.modelsTo = Collections.unmodifiableSet(new HashSet<>(modelsTo));
    this.conditions = Collections.unmodifiableSet(new HashSet<>(conditions));
  }

  public String getModelFor() {
    return modelFor;
  }

  public Set<String> getModelsTo() {
    return modelsTo;
  }

  public Set<String> getConditions() {
    return conditions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelFor, modelsTo, conditions);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ComparisonCondition that = (ComparisonCondition) o;

    return Objects.equals(modelFor, that.modelFor) && Objects.equals(modelsTo, that.modelsTo)
        && Objects.equals(conditions, that.conditions);
  }
}
