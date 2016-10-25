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

/**
 * @author : ashutosh 
 * 29/04/2016
 */
public class PatternTestModel {
  @Pattern(regexp = "[a-zA-Z0-9_]")
  private String patternString;

  private String noPatternString;

  private String getterPatternString;

  public String getPatternString() {
    return patternString;
  }

  public String getNoPatternString() {
    return noPatternString;
  }

  @Pattern(regexp = "[A-Z]")
  public String getGetterPatternString() {
    return getterPatternString;
  }

  public void setPatternString(String patternString) {
    this.patternString = patternString;
  }

  public void setNoPatternString(String noPatternString) {
    this.noPatternString = noPatternString;
  }

  public void setGetterPatternString(String getterPatternString) {
    this.getterPatternString = getterPatternString;
  }
}
