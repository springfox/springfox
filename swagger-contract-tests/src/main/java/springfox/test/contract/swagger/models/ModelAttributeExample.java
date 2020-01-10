/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import java.util.List;

public class ModelAttributeExample {
  private String stringProp;
  private int intProp;
  private List<String> listProp;
  private int[] arrayProp;
  private Category complexProp;
  private List<AccountType> accountTypes;

  public String getStringProp() {
    return stringProp;
  }

  public void setStringProp(String stringProp) {
    this.stringProp = stringProp;
  }

  public int getIntProp() {
    return intProp;
  }

  public void setIntProp(int intProp) {
    this.intProp = intProp;
  }

  public List<String> getListProp() {
    return listProp;
  }

  public void setListProp(List<String> listProp) {
    this.listProp = listProp;
  }

  public int[] getArrayProp() {
    return arrayProp;
  }

  public void setArrayProp(int[] arrayProp) {
    this.arrayProp = arrayProp;
  }

  public Category getComplexProp() {
    return complexProp;
  }

  public void setComplexProp(Category complexProp) {
    this.complexProp = complexProp;
  }

  public List<AccountType> getAccountTypes() {
    return accountTypes;
  }

  public void setAccountTypes(List<AccountType> accountTypes) {
    this.accountTypes = accountTypes;
  }
}
