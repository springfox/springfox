/*
 *
 *  Copyright 2017 the original author or authors.
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

package springfox.test.contract.swagger.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModelProperty;

public class SameCategory extends Category {

  @ApiModelProperty(value = "Id field", required = true)
  private Integer id;

  @ApiModelProperty(value = "Type field", readOnly = true)
  private String type;

  public SameCategory(String name, Integer id, String type) {
    super(name);
    this.id = id;
    this.type = type;
  }

  @JsonProperty(value = "_id", access = Access.WRITE_ONLY)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty(value = "_type", access = Access.READ_ONLY)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}