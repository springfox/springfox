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

public class ArraysContainer {
  private ComplexType[] complexTypes;
  private ExampleEnum[] enums;
  private Integer[] integers;
  private String[] strings;
  private Object[] objects;
  private byte[] bytes;

  ArraysContainer(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ComplexType[] getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ExampleEnum[] getEnums() {
    return enums;
  }

  public void setEnums(ExampleEnum[] enums) {
    this.enums = enums;
  }

  public Integer[] getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(Integer[] years) {
    this.integers = years;
  }

  public String[] getStrings() {
    return strings;
  }

  public void setStrings(String[] strings) {
    this.strings = strings;
  }

  public Object[] getObjects() {
    return objects;
  }

  public void setObjects(Object[] objects) {
    this.objects = objects;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
}

