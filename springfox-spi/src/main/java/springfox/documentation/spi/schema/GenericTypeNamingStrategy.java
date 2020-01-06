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

package springfox.documentation.spi.schema;

/**
 * Strategy to provide the strings used while naming generic types in the swagger output
 */
public interface GenericTypeNamingStrategy {
  /**
   * @return the string used to denote the beginning of a generic i.e. the &lt; in List&lt;String&gt;
   */
  String getOpenGeneric();

  /**
   * @return the string used to denote the beginning of a generic i.e. the &gt; in List&lt;String&gt;
   */
  String getCloseGeneric();

  /**
   * @return the string used to denote the beginning of a generic i.e. the , in Map&lt;String,String&gt;
   */
  String getTypeListDelimiter();
}
