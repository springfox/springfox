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

package springfox.documentation.spring.web.dummy.models;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

public class ModelAttributeWithHiddenParametersExample extends ModelAttributeExample {
  @ApiParam(hidden = true)
  private Integer category;
  private String[] modelAttributeProperty;
  @ApiModelProperty(hidden = true)
  private String hiddenProperty1;
  @ApiModelProperty(hidden = true)
  private String hiddenProperty2;

  public Integer getCategory() {
    return category;
  }

  public void setCategory(Integer category) {
    this.category = category;
  }

  public String[] getModelAttributeProperty() {
    return modelAttributeProperty;
  }

  public void setModelAttributeProperty(String[] modelAttributeProperty) {
    this.modelAttributeProperty = modelAttributeProperty;
  }

  public String getHiddenProperty1() {
    return hiddenProperty1;
  }

  public void setHiddenProperty1(String hiddenProperty1) {
    this.hiddenProperty1 = hiddenProperty1;
  }

  public String getHiddenProperty2() {
    return hiddenProperty2;
  }

  public void setHiddenProperty2(String hiddenProperty2) {
    this.hiddenProperty2 = hiddenProperty2;
  }
}
