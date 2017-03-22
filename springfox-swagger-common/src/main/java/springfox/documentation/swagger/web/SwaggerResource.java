/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SwaggerResource implements Comparable<SwaggerResource> {
  private String name;
  private String location;
  private String swaggerVersion;

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @JsonProperty("swaggerVersion")
  public String getSwaggerVersion() {
    return swaggerVersion;
  }

  public void setSwaggerVersion(String swaggerVersion) {
    this.swaggerVersion = swaggerVersion;
  }

  @Override
  public int compareTo(SwaggerResource other) {
    return ComparisonChain.start()
        .compare(this.swaggerVersion, other.swaggerVersion)
        .compare(this.name, other.name)
        .result();
  }
}