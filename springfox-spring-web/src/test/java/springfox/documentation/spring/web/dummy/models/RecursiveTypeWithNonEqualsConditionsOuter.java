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
package springfox.documentation.spring.web.dummy.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class RecursiveTypeWithNonEqualsConditionsOuter {

  @JsonProperty(access = Access.WRITE_ONLY)
  private String conditionalProperty;

  private RecursiveTypeWithNonEqualsConditionsMiddle recursiveTypeWithNonEqualsConditionsMiddle;

  public RecursiveTypeWithNonEqualsConditionsMiddle getRecursiveTypeWithNonEqualsConditionsMiddle() {
    return recursiveTypeWithNonEqualsConditionsMiddle;
  }

  public void setRecursiveTypeWithNonEqualsConditionsMiddle(
      RecursiveTypeWithNonEqualsConditionsMiddle recursiveTypeWithNonEqualsConditionsMiddle) {
    this.recursiveTypeWithNonEqualsConditionsMiddle = recursiveTypeWithNonEqualsConditionsMiddle;
  }

  public String getConditionalProperty() {
    return conditionalProperty;
  }

  public void setConditionalProperty(String conditionalProperty) {
    this.conditionalProperty = conditionalProperty;
  }

}
