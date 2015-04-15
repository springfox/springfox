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

package springfox.documentation.swagger1.configuration;

public class SimpleBean {
  private String aValue;
  private String anotherValue;

  public SimpleBean() {
  }

  public String getaValue() {
    return aValue;
  }

  public void setaValue(String aValue) {
    this.aValue = aValue;
  }

  public String getAnotherValue() {
    return anotherValue;
  }

  public void setAnotherValue(String anotherValue) {
    this.anotherValue = anotherValue;
  }
}
