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

package springfox.documentation.schema;

import java.util.ArrayList;
import java.util.List;

public class ListsContainer {
  private List<ToSubstitute> substituted;
  private List<ComplexType> complexTypes;
  private List<ExampleEnum> enums;
  private List<Integer> integers;
  private ArrayList<String> strings;
  private List<Object> objects;

  ListsContainer(List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public List<ExampleEnum> getEnums() {
    return enums;
  }

  public void setEnums(List<ExampleEnum> enums) {
    this.enums = enums;
  }

  public List<Integer> getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(List<Integer> years) {
    this.integers = years;
  }

  public ArrayList<String> getStrings() {
    return strings;
  }

  public void setStrings(ArrayList<String> strings) {
    this.strings = strings;
  }

  public List<Object> getObjects() {
    return objects;
  }

  public void setObjects(List<Object> objects) {
    this.objects = objects;
  }

  public List<ToSubstitute> getSubstituted() {
    return substituted;
  }

  public void setSubstituted(List<ToSubstitute> substituted) {
    this.substituted = substituted;
  }
}
