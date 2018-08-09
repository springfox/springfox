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

package springfox.petstore.webflux.model;

import io.swagger.annotations.ApiModelProperty;
import springfox.petstore.webflux.repository.Identifiable;

import java.util.ArrayList;
import java.util.List;

public class Pet implements Identifiable<Long> {
  private long id;
  private Category category;
  private String name;
  private List<String> photoUrls = new ArrayList<String>();
  private List<Tag> tags = new ArrayList<Tag>();
  private String status;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  //  @XmlElementWrapper(name = "photoUrls")
//  @XmlElement(name = "photoUrl")
  public List<String> getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(List<String> photoUrls) {
    this.photoUrls = photoUrls;
  }

  //  @XmlElementWrapper(name = "tags")
//  @XmlElement(name = "tag")
  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  @ApiModelProperty(value = "pet status in the store", allowableValues = "available,pending,sold")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public Long getIdentifier() {
    return id;
  }
}
