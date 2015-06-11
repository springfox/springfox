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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class TypeForTestingPropertyNames {
  public int getProp() {
    throw new UnsupportedOperationException();
  }
  public int getProp1() {
    throw new UnsupportedOperationException();
  }
  public int getProp_1() {
    throw new UnsupportedOperationException();
  }
  public int isProp() {
    throw new UnsupportedOperationException();
  }
  public int isProp1() {
    throw new UnsupportedOperationException();
  }
  public int isProp_1() {
    throw new UnsupportedOperationException();
  }
  public void setProp(int a) {
    throw new UnsupportedOperationException();
  }
  public void setProp1(int a) {
    throw new UnsupportedOperationException();
  }
  public void setProp_1(int a) {
    throw new UnsupportedOperationException();
  }
  public int prop() {
    throw new UnsupportedOperationException();
  }
  @JsonGetter("prop")
  public int getAnotherProp() {
    throw new UnsupportedOperationException();
  }
  @JsonSetter("prop")
  public void setAnotherProp(int a) {
    throw new UnsupportedOperationException();
  }
  @JsonSetter("prop2")
  public void anotherProp(int a) {
    throw new UnsupportedOperationException();
  }
  @JsonGetter("prop3")
  public int yetAnotherProp() {
    throw new UnsupportedOperationException();
  }
  @JsonGetter
  public int getPropFallback() {
    throw new UnsupportedOperationException();
  }
  @JsonSetter
  public void setPropFallback(int a) {
    throw new UnsupportedOperationException();
  }
}
