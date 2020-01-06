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
package springfox.bean.validators.plugins.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NullabilityTestModel {
  @NotNull
  private String notNullString;
  private String string;
  private String notNullGetter;
  @NotBlank
  private String notBlankString;
  private String notBlankGetter;

  public String getNotNullString() {
    return notNullString;
  }

  public String getString() {
    return string;
  }

  @NotNull
  public String getNotNullGetter() {
    return notNullGetter;
  }

  public void setNotNullString(String notNullString) {
    this.notNullString = notNullString;
  }

  public void setString(String string) {
    this.string = string;
  }

  public void setNotNullGetter(String notNullGetter) {
    this.notNullGetter = notNullGetter;
  }

  @NotBlank
  public String getNotBlankGetter() {
    return notBlankGetter;
  }

  public String getNotBlankString() {
    return notBlankString;
  }

  public void setNotBlankString(String notBlankString) {
    this.notBlankString = notBlankString;
  }

  public void setNotBlankGetter(String notBlankGetter) {
    this.notBlankGetter = notBlankGetter;
  }

}
