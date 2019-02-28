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

import io.swagger.annotations.ApiModelProperty;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Map;

public class TypeWithAnnotatedGettersAndSetters implements TypeWithAnnotatedGettersAndSettersInterface {
  @ApiModelProperty(notes = "int Property Field", required = true)
  private int intProp;
  private boolean boolProp;
  private ExampleEnum enumProp;
  private GenericType<String> genericProp;
  private int hiddenProp;
  private LocalDate validOverride;
  private LocalDate invalidOverride;
  private int readOnlyProp;
  @ApiModelProperty(value = "Some description")
  private List<String> listOfStrings;
  private Map<String, Map<String, Foo>> mapOfMaps;
  private int interfaceProp;

  public int getIntProp() {
    return intProp;
  }

  public void setIntProp(int intProp) {
    this.intProp = intProp;
  }

  @ApiModelProperty(notes = "bool Property Getter", required = false)
  public boolean isBoolProp() {
    return boolProp;
  }

  public void setBoolProp(boolean boolProp) {
    this.boolProp = boolProp;
  }

  public void getVoid() {
  }

  public int isNotGetter() {
    return 0;
  }

  public int getWithParam(int param) {
    return 0;
  }

  public int setNotASetter() {
    return 0;
  }

  @ApiModelProperty(value = "enum Prop Getter value", notes = "enum note", allowableValues = "ONE", required = true,
      readOnly = false)
  public ExampleEnum getEnumProp() {
    return enumProp;
  }

  public void setEnumProp(ExampleEnum enumProp) {
    this.enumProp = enumProp;
  }

  @ApiModelProperty(hidden = true)
  public int getHiddenProp() {
    return hiddenProp;
  }

  @ApiModelProperty(value = "readOnly property getter", readOnly = true)
  public int getReadOnlyProp() {
    return readOnlyProp;
  }

  @ApiModelProperty(dataType = "UnknownType")
  public LocalDate getInvalidOverride() {
    return invalidOverride;
  }

  @ApiModelProperty(dataType = "java.lang.String")
  public LocalDate getValidOverride() {
    return validOverride;
  }

  @ApiModelProperty
  public Map<String, Map<String, Foo>> getMapOfMaps() {
    return mapOfMaps;
  }

  public void setMapOfMaps(Map<String, Map<String, Foo>> mapOfMaps) {
    this.mapOfMaps = mapOfMaps;
  }

  @ApiModelProperty(value = "Some description")
  public List<String> getListOfStrings() {
    return listOfStrings;
  }

  public void setListOfStrings(List<String> listOfStrings) {
    this.listOfStrings = listOfStrings;
  }

  public int getInterfaceProp() {
    return interfaceProp;
  }

  public void setInterfaceProp(int interfaceProp) {
    this.interfaceProp = interfaceProp;
  }

  @SuppressWarnings("VisibilityModifier")
  class Foo {
    public Integer fooInt;
  }
}
