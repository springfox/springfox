/*
 *
 *  Copyright 2018 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonView;

public class TypeWithJsonView {

  @JsonView(Views.FirstView.class)
  private String foo;
  
  @JsonView(Views.SecondView.class)
  private Integer bar;
  
  private String propertyWithoutView;

  public TypeWithJsonView(String foo, Integer bar, String propertyWithoutView) {
    this.foo = foo;
    this.bar = bar;
    this.propertyWithoutView = propertyWithoutView;
  }

  public void setPropertyWithoutView(String propertyWithoutView) {
    this.propertyWithoutView = propertyWithoutView;
  }

  public void setFoo(String foo) {
    this.foo = foo;
  }

  public void setBar(Integer bar) {
    this.bar = bar;
  }

  public String getPropertyWithoutView() {
    return propertyWithoutView;
  }

  public String getFoo() {
    return foo;
  }

  public Integer getBar() {
    return bar;
  }
}
