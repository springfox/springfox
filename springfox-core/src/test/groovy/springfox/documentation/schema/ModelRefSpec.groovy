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

package springfox.documentation.schema

import spock.lang.Specification
import spock.lang.Unroll

class ModelRefSpec extends Specification {
  @Unroll
  def "map types are reflected correctly" () {
    expect:
      model.isCollection() == isCollection
      model.isMap() == isMap
    where:
      model                                   | isCollection  | isMap
      new ModelRef("string")                  | false         | false
      new ModelRef("string", null)            | false         | false
      new ModelRef("string", null, true)      | false         | false
      new ModelRef("string", "List", true)    | false         | true
      new ModelRef("string", "List", false)   | true          | false
      new ModelRef("string", "Map", true)     | false         | true
      new ModelRef("string", "Map", false)    | true          | false
  }
}
