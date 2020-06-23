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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Objects;

public class Pet {

  @JsonProperty("id")

  private Long id;


  @JsonProperty("name")

  private String name;


  @JsonProperty("category")

  private Category category;


  @JsonProperty("photoUrls")

  @javax.validation.Valid
  private java.util.List<String> photoUrls = new ArrayList<>();


  @JsonProperty("tags")

  @javax.validation.Valid
  private java.util.List<Tag> tags = null;

  @JsonProperty("status")

  private StatusEnum status;

  public Pet id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   *
   * @return id
   */
  @io.swagger.v3.oas.annotations.media.Schema(example = "10", description = "")


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Pet name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   *
   * @return name
   */
  @io.swagger.v3.oas.annotations.media.Schema(example = "doggie", required = true, description = "")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Pet category(Category category) {
    this.category = category;
    return this;
  }

  /**
   * Get category
   *
   * @return category
   */
  @io.swagger.v3.oas.annotations.media.Schema(description = "")

  @javax.validation.Valid

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Pet photoUrls(java.util.List<String> photoUrls) {
    this.photoUrls = photoUrls;
    return this;
  }

  public Pet addPhotoUrlsItem(String photoUrlsItem) {
    this.photoUrls.add(photoUrlsItem);
    return this;
  }

  /**
   * Get photoUrls
   *
   * @return photoUrls
   */
  @io.swagger.v3.oas.annotations.media.Schema(required = true, description = "")
  @NotNull


  public java.util.List<String> getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(java.util.List<String> photoUrls) {
    this.photoUrls = photoUrls;
  }

  public Pet tags(java.util.List<Tag> tags) {
    this.tags = tags;
    return this;
  }

  public Pet addTagsItem(Tag tagsItem) {
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * Get tags
   *
   * @return tags
   */
  @io.swagger.v3.oas.annotations.media.Schema(description = "")

  @javax.validation.Valid

  public java.util.List<Tag> getTags() {
    return tags;
  }

  public void setTags(java.util.List<Tag> tags) {
    this.tags = tags;
  }

  public Pet status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * pet status in the store
   *
   * @return status
   */
  @io.swagger.v3.oas.annotations.media.Schema(description = "pet status in the store")


  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pet pet = (Pet) o;
    return Objects.equals(this.id, pet.id) &&
        Objects.equals(this.name, pet.name) &&
        Objects.equals(this.category, pet.category) &&
        Objects.equals(this.photoUrls, pet.photoUrls) &&
        Objects.equals(this.tags, pet.tags) &&
        Objects.equals(this.status, pet.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, category, photoUrls, tags, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Pet {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    photoUrls: ").append(toIndentedString(photoUrls)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

  /**
   * pet status in the store
   */
  public enum StatusEnum {
    AVAILABLE("available"),

    PENDING("pending"),

    SOLD("sold");

    private final String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }
}

