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
package springfox.documentation.util;

public abstract class Equivalence<T> {

  private T delegate;
  
  public Equivalence(T delegate) {
    this.delegate = delegate;
  }
  
  public T get() {
    return delegate;
  }
  
  @Override
  public int hashCode() {
    if (delegate == null) {
      return 0;
    }
    return doHash(delegate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (this == null || obj == null) {
      return false;
    }
    // Mocks provide directly the object
    // probably change tests...
//    if (getClass() != obj.getClass()) {
//      return false;
//    }
    T other = null;
    if (obj instanceof Equivalence) {
      other = (T)((Equivalence)obj).get();
    } else {
      other = (T)obj;
    }
    if(delegate == other) {
      return true;
    }
    if(delegate == null || other == null) {
      return false;
    }
    return doEquivalent(delegate, other);
  }
  
  protected abstract boolean doEquivalent(T a, T b);
  protected abstract int doHash(T t);
}
