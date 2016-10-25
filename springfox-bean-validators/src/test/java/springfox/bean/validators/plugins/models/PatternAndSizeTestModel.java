/*
 *
 *  Copyright 2016 the original author or authors.
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

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author : ashutosh
 *         18/05/2016
 */
public class PatternAndSizeTestModel {

  @Size(min = 3, max = 5)
  @Pattern(regexp = "[a-zA-Z0-9_]")
  private String propertyString;

  private String getterString;

  public String getPropertyString() {
    return propertyString;
  }

  @Size(min = 1, max = 4)
  @Pattern(regexp = "[A-Z]")
  public String getGetterString() {
    return getterString;
  }

  public void setPropertyString(String propertyString) {
    this.propertyString = propertyString;
  }

  public void setGetterString(String getterString) {
    this.getterString = getterString;
  }
}
