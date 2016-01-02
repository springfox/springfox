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

package springfox.documentation.swagger1.mappers

import spock.lang.Specification
import springfox.documentation.schema.ModelRef
import springfox.documentation.swagger1.mixins.MapperSupport

@Mixin(MapperSupport)
class DataTypeMapperSpec extends Specification {

  def "Maps type name of model ref for responseTypeName"() {
    given:
      def sut = dataTypeMapper()
    when:
      def mapped = sut.responseTypeName(modelRef)
    then:
      mapped == typeName
    where:
      modelRef                                      | typeName
      new ModelRef("void")                          | "void"
      new ModelRef("List", new ModelRef("String"))  | "array"
      null                                          | null
  }

  def "Maps type name of model ref for typeFromModelRef"() {
    given:
      def sut = dataTypeMapper()
    when:
      def mapped = sut.typeFromModelRef(modelRef)
    then:
      mapped?.absoluteType == typeName
    
    where:
      modelRef                                      | typeName
      new ModelRef("void")                          | "void"
      new ModelRef("List", new ModelRef("String"))  | "array"
      null                                          | null
  }

  def "Maps model ref for operationTypeFromModelRef"() {
    given:
      def sut = dataTypeMapper()
    when:
      def mapped = sut.operationTypeFromModelRef(modelRef)
    then:
      mapped?.absoluteType == typeName

    where:
      modelRef                                      | typeName
      new ModelRef("void")                          | "void"
      new ModelRef("List", new ModelRef("String"))  | "array"
      null                                          | null
  }
}
