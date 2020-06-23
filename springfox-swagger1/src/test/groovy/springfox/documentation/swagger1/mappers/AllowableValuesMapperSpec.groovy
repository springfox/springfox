/*
 *
 *  Copyright 2017 the original author or authors.
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
import springfox.documentation.service.AllowableValues
import springfox.documentation.swagger1.dto.AllowableListValues
import springfox.documentation.swagger1.dto.AllowableRangeValues
import springfox.documentation.swagger1.mixins.MapperSupport

class AllowableValuesMapperSpec extends Specification implements MapperSupport {
  
  def "Maps null range input to null output"() {
    given:
      springfox.documentation.service.AllowableRangeValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto == source
  }

  def "Maps null list values input to null output"() {
    given:
      springfox.documentation.service.AllowableListValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto == source
  }

  def "Maps list values input when values is null"() {
    given:
      springfox.documentation.service.AllowableListValues source = new springfox.documentation.service.AllowableListValues(null, "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values == null
      dto.valueType == "string"
  }

  def "Maps allowable list correctly"() {
    given:
      springfox.documentation.service.AllowableListValues source = new springfox.documentation.service.AllowableListValues(['ONE', 'TWO'], "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values.containsAll(['ONE', 'TWO'])
      dto.valueType == "string"
  }

  def "Maps allowable ranges correctly"() {
    given:
      springfox.documentation.service.AllowableRangeValues source = new springfox.documentation.service.AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto.min == "0"
      dto.max == "5"
  }

  def "Maps abstract values correctly correctly"() {
    given:
      springfox.documentation.service.AllowableListValues listSource = new springfox.documentation.service.AllowableListValues(['ONE', 'TWO'], "string")
      springfox.documentation.service.AllowableRangeValues rangeSource = new springfox.documentation.service.AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      def rangeDto = mapper.toSwaggerAllowableValues(rangeSource)
      def listDto = mapper.toSwaggerAllowableValues(listSource)
      def nullDto = mapper.toSwaggerAllowableValues(null)
    then:
      rangeDto instanceof AllowableRangeValues
      rangeDto.min == "0"
      rangeDto.max == "5"
      listDto instanceof AllowableListValues
      listDto.values.containsAll(['ONE', 'TWO'])
      listDto.valueType == "string"
      nullDto == null
  }

  def "AllowableValuesMapper handles unmapped values type"() {
    given:
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      mapper.toSwaggerAllowableValues(new AllowableValues() {})
    then:
      thrown(UnsupportedOperationException)
  }
}
