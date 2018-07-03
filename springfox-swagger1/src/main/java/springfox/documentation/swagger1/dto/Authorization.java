/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger1.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


public class Authorization {
  private String type;
  private List<AuthorizationScope> scopes;

  public Authorization() {
  }

  public Authorization(String type, AuthorizationScope[] scopes) {
    this.scopes = Stream.of(scopes).collect(toList());
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public void setScopes(List<AuthorizationScope> scopes) {
    this.scopes = new ArrayList<>(scopes);
  }
}
