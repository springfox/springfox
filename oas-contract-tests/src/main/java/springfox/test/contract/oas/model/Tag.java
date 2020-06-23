/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package springfox.test.contract.oas.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Tag
 */

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
                            date = "2019-11-30T09:49:26.034469-01:00[Atlantic/Azores]")


public class Tag {

  @JsonProperty("id")

  private Long id;


  @JsonProperty("name")

  private String name;


  public Tag id(Long id) {
    this.id = id;
    return this;
  }


  /**
   * Get id
   *
   * @return id
   */
  @io.swagger.v3.oas.annotations.media.Schema(description = "")


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public Tag name(String name) {
    this.name = name;
    return this;
  }


  /**
   * Get name
   *
   * @return name
   */
  @io.swagger.v3.oas.annotations.media.Schema(description = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Tag tag = (Tag) o;
    return Objects.equals(this.id, tag.id) &&
        Objects.equals(this.name, tag.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Tag {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

