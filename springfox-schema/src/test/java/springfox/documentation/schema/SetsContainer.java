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

import java.util.HashSet;
import java.util.Set;

public class SetsContainer {
  private Set<ComplexType> complexTypes;
  private Set<ExampleEnum> enums;
  private Set<Integer> integers;
  private HashSet<String> strings;
  private Set<Object> objects;

  SetsContainer(Set<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public Set<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(Set<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public Set<ExampleEnum> getEnums() {
    return enums;
  }

  public void setEnums(Set<ExampleEnum> enums) {
    this.enums = enums;
  }

  public Set<Integer> getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(Set<Integer> years) {
    this.integers = years;
  }

  public HashSet<String> getStrings() {
    return strings;
  }

  public void setStrings(HashSet<String> strings) {
    this.strings = strings;
  }

  public Set<Object> getObjects() {
    return objects;
  }

  public void setObjects(Set<Object> objects) {
    this.objects = objects;
  }
}
