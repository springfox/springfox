/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by yeh on 22.05.2017.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumObjectType {
  ONE("One", "This in an enum for number 1"), TWO("Two", "This in an enum for number 2");

  private String name;
  private String description;

  EnumObjectType(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }
}
