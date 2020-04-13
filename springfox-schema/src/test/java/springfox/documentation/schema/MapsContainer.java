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

import java.util.Map;

public class MapsContainer {
  private Map<ExampleEnum, SimpleType> enumToSimpleType;
  private Map<String, SimpleType> stringToSimpleType;
  private Map<Category, SimpleType> complexToSimpleType;
  private Map<Category, String> complexToString;
  private Map<String, Map<String, SimpleType>> mapOfmapOfStringToSimpleType;

  public Map<ExampleEnum, SimpleType> getEnumToSimpleType() {
    return enumToSimpleType;
  }

  public void setEnumToSimpleType(Map<ExampleEnum, SimpleType> enumToSimpleType) {
    this.enumToSimpleType = enumToSimpleType;
  }

  public Map<String, SimpleType> getStringToSimpleType() {
    return stringToSimpleType;
  }

  public void setStringToSimpleType(Map<String, SimpleType> stringToSimpleType) {
    this.stringToSimpleType = stringToSimpleType;
  }

  public Map<Category, SimpleType> getComplexToSimpleType() {
    return complexToSimpleType;
  }

  public void setComplexToSimpleType(Map<Category, SimpleType> complexToSimpleType) {
    this.complexToSimpleType = complexToSimpleType;
  }

  public Map<String, Map<String, SimpleType>> getMapOfmapOfStringToSimpleType() {
    return mapOfmapOfStringToSimpleType;
  }

  public void setMapOfmapOfStringToSimpleType(Map<String, Map<String, SimpleType>> mapOfmapOfStringToSimpleType) {
    this.mapOfmapOfStringToSimpleType = mapOfmapOfStringToSimpleType;
  }

  public Map<Category, String> getComplexToString() {
    return complexToString;
  }

  public void setComplexToString(Map<Category, String> complexToString) {
    this.complexToString = complexToString;
  }
}
