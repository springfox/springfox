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
import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification

class MapsSpec extends Specification {
  @Shared def resolver = new TypeResolver()
  def "detects that a given type is a Map" () {
    given:
      Maps.isMapType(resolver.resolve(type))
    where:
      type                                      | isMap
      String                                    | false
      SimpleType                                | false
      resolver.resolve(List, String)            | false
      resolver.resolve(Map, String, SimpleType) | true
      resolver.resolve(Map, String, Object)     | true
      resolver.resolve(Map, Object, Object)     | true
      resolver.resolve(Map)                     | true
  }

  def "provides value type of the map" () {
    given:
      Maps.mapValueType(resolver.resolve(type)).getErasedSignature().equals(name)
    where:
      type                                      | name
      String                                    | "Object"
      SimpleType                                | "Object"
      resolver.resolve(List, String)            | "Object"
      resolver.resolve(Map, String, SimpleType) | "SimpleType"
      resolver.resolve(Map, String, Object)     | "Object"
      resolver.resolve(Map, Object, Object)     | "Object"
      resolver.resolve(Map, Object, SimpleType) | "SimpleType"
      resolver.resolve(Map)                     | "Object"
  }
}
