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

public class TypeWithGettersAndSetters {
  private int intProp;
  private boolean boolProp;
  private ExampleEnum enumProp;
  private GenericType<String> genericProp;
  private GenericType<byte[]> genericByteArray;
  private GenericType<Category[]> genericCategoryArray;

  public int getIntProp() {
    return intProp;
  }

  public void setIntProp(int intProp) {
    this.intProp = intProp;
  }

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

  public GenericType<Category[]> getGenericCategoryArray() {
    return genericCategoryArray;
  }

  public void setGenericCategoryArray(GenericType<Category[]> genericCategoryArray) {
    this.genericCategoryArray = genericCategoryArray;
  }

  public GenericType<byte[]> getGenericByteArray() {
    return genericByteArray;
  }

  public void setGenericByteArray(GenericType<byte[]> genericByteArray) {
    this.genericByteArray = genericByteArray;
  }
}
