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

public class Assert {

  // REVISIT: We keep interface like done before, but if Exception class is not important
  // we could change to Spring Assert class instead and remove this.
  public static String checkNotNull(String obj, String msg) {
    if (obj == null) {
      throw new NullPointerException(msg);
    }
    return obj;
  }
  
  public static void checkArgument(boolean b, String msg) {
    if (!b) {
      throw new IllegalArgumentException(msg);
    }
  }
}
