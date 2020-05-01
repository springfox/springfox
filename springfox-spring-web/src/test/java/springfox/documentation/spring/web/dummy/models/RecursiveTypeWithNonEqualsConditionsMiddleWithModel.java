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

public class RecursiveTypeWithNonEqualsConditionsMiddleWithModel {

  private RecursiveTypeWithNonEqualsConditionsMiddleWithModel recursiveTypeWithNonEqualsConditionsMiddleWithModel;

  private RecursiveTypeWithNonEqualsConditionsInnerWithModel recursiveTypeWithNonEqualsConditionsInnerWithModel;

  @JsonProperty(access = Access.WRITE_ONLY)
  private Pet pet;

  public RecursiveTypeWithNonEqualsConditionsMiddleWithModel getRecursiveTypeWithNonEqualsConditionsMiddleWithModel() {
    return recursiveTypeWithNonEqualsConditionsMiddleWithModel;
  }

  public void setRecursiveTypeWithNonEqualsConditionsMiddleWithModel(
      RecursiveTypeWithNonEqualsConditionsMiddleWithModel recursiveTypeWithNonEqualsConditionsMiddleWithModel) {
    this.recursiveTypeWithNonEqualsConditionsMiddleWithModel = recursiveTypeWithNonEqualsConditionsMiddleWithModel;
  }

  public RecursiveTypeWithNonEqualsConditionsInnerWithModel getRecursiveTypeWithNonEqualsConditionsInnerWithModel() {
    return recursiveTypeWithNonEqualsConditionsInnerWithModel;
  }

  public void setRecursiveTypeWithNonEqualsConditionsInnerWithModel(
      RecursiveTypeWithNonEqualsConditionsInnerWithModel recursiveTypeWithNonEqualsConditionsInnerWithModel) {
    this.recursiveTypeWithNonEqualsConditionsInnerWithModel = recursiveTypeWithNonEqualsConditionsInnerWithModel;
  }

  public Pet getPet() {
    return pet;
  }

  public void setPet(Pet pet) {
    this.pet = pet;
  }

}
