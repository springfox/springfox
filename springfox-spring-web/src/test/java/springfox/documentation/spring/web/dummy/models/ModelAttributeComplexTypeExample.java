/*
 *
 *  Copyright 2016-2017 the original author or authors.
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

import java.util.List;

public class ModelAttributeComplexTypeExample extends ModelAttributeExample {

  private List<FancyPet> fancyPets;

  private Category[] categories;

  private String[] modelAttributeProperty;

  private List<ModelAttributeComplexTypeExample> recursiveList;

  public List<FancyPet> getFancyPets() {
    return fancyPets;
  }

  public void setFancyPets(List<FancyPet> fancyPets) {
    this.fancyPets = fancyPets;
  }

  public Category[] getCategories() {
    return categories;
  }

  public void setCategories(Category[] categories) {
    this.categories = categories;
  }

  public String[] getModelAttributeProperty() {
    return modelAttributeProperty;
  }

  public void setModelAttributeProperty(String[] modelAttributeProperty) {
    this.modelAttributeProperty = modelAttributeProperty;
  }

  public List<ModelAttributeComplexTypeExample> getRecursiveList() {
    return recursiveList;
  }

  public void setRecursiveList(List<ModelAttributeComplexTypeExample> recursiveList) {
    this.recursiveList = recursiveList;
  }
}
