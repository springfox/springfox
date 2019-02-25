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

import java.util.List;

public class GenericTypeBoundToMultiple<A, B> {
  private final A a;
  private final List<B> listOfB;
  private final B[] arrayOfB;

  public GenericTypeBoundToMultiple(List<B> listOfB, A a, B[] arrayOfB) {
    this.listOfB = listOfB;
    this.a = a;
    this.arrayOfB = arrayOfB;
  }

  public A getA() {
    return a;
  }

  public List<B> getListOfB() {
    return listOfB;
  }

  public B[] getArrayOfB() {
    return arrayOfB;
  }
}
