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

package springfox.test.contract.swagger.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class Example extends Parent implements Serializable {

  private static final long serialVersionUID = -8084678021874483017L;

  @ApiParam(value = "description of foo", required = true, allowableValues = "man,chu")
  private String foo;

  @ApiModelProperty(value = "description of bar", required = false, example = "10")
  private int bar;

  private EnumType enumType;

  @ApiModelProperty(value = "A read only string", readOnly = true)
  private String readOnlyString;

  @ApiParam(value = "description of annotatedEnumType", required = false)
  private EnumType annotatedEnumType;

  private NestedType nestedType;

  @JsonProperty("propertyWithNoGetterMethod")
  private String propertyWithNoGetterMethod;
  private String propertyWithNoSetterMethod;

  @ApiParam(value = "local date time desc dd-MM-yyyy hh:mm:ss", required = true)
  private LocalDateTime localDateTime;
  @ApiParam(value = "description of allCapsSet", required = false)
  private CustomAllCapsStringHashSet allCapsSet;

  private Void voidParam;

  public Example(String foo, int bar, EnumType enumType, NestedType nestedType) {
    this.foo = foo;
    this.bar = bar;
    this.enumType = enumType;
    this.nestedType = nestedType;
  }

  public String getFoo() {
    return foo;
  }

  public void setFoo(String foo) {
    this.foo = foo;
  }

  public int getBar() {
    return bar;
  }

  public void setBar(int bar) {
    this.bar = bar;
  }

  public EnumType getEnumType() {
    return enumType;
  }

  public void setEnumType(EnumType enumType) {
    this.enumType = enumType;
  }

  public EnumType getAnnotatedEnumType() {
    return annotatedEnumType;
  }

  public void setAnnotatedEnumType(EnumType annotatedEnumType) {
    this.annotatedEnumType = annotatedEnumType;
  }

  public NestedType getNestedType() {
    return nestedType;
  }

  public void setNestedType(NestedType nestedType) {
    this.nestedType = nestedType;
  }

  public void setPropertyWithNoGetterMethod(String propertyWithNoGetterMethod) {
    this.propertyWithNoGetterMethod = propertyWithNoGetterMethod;
  }

  public String getPropertyWithNoSetterMethod() {
    return this.propertyWithNoSetterMethod;
  }

  public CustomAllCapsStringHashSet getAllCapsSet() {
    return allCapsSet;
  }

  public void setAllCapsSet(CustomAllCapsStringHashSet allCapsSet) {
    this.allCapsSet = allCapsSet;
  }

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }

  public void setLocalDateTime(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  public String getReadOnlyString() {
    return readOnlyString;
  }

  public void setReadOnlyString(String readOnlyString) {
    this.readOnlyString = readOnlyString;
  }

  public Void getVoidParam() {
    return voidParam;
  }

  public void setVoidParam(Void voidParam) {
    this.voidParam = voidParam;
  }

  class InnerSynthetic {
  }
}

