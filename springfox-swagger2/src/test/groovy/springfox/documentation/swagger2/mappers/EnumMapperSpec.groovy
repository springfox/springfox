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
package springfox.documentation.swagger2.mappers

import io.swagger.models.parameters.QueryParameter
import io.swagger.models.properties.DoubleProperty
import io.swagger.models.properties.FloatProperty
import io.swagger.models.properties.IntegerProperty
import io.swagger.models.properties.LongProperty
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.AllowableRangeValues

import static springfox.documentation.swagger2.mappers.EnumMapper.*

class EnumMapperSpec extends Specification {
  @Unroll("#property.class.simpleName")
  def "adds enum values for numeric properties" () {
    when:
      maybeAddAllowableValues(property, allowableValues)
    then:
      if (allowableValues instanceof AllowableListValues) {
        assert property.enum.size() == expectedSize
      } else if (allowableValues instanceof AllowableRangeValues) {
        assert property.minimum == 1D
        assert property.maximum == 2D
      } else {
        assert property.enum == null
      }
    where:
      property              | expectedSize | allowableValues
      new IntegerProperty() | 0            | new AllowableListValues(["A", "B", "C"], "List")
      new LongProperty()    | 0            | new AllowableListValues(["A", "B", "C"], "List")
      new DoubleProperty()  | 0            | new AllowableListValues(["A", "B", "C"], "List")
      new FloatProperty()   | 0            | new AllowableListValues(["A", "B", "C"], "List")
      new IntegerProperty() | 3            | new AllowableListValues(["1", "2", "3"], "List")
      new LongProperty()    | 3            | new AllowableListValues(["1", "2", "3"], "List")
      new DoubleProperty()  | 3            | new AllowableListValues(["1", "2", "3"], "List")
      new FloatProperty()   | 3            | new AllowableListValues(["1", "2", "3"], "List")
      new IntegerProperty() | 0            | new AllowableRangeValues("1", "2")
      new LongProperty()    | 0            | new AllowableRangeValues("1", "2")
      new DoubleProperty()  | 0            | new AllowableRangeValues("1", "2")
      new FloatProperty()   | 0            | new AllowableRangeValues("1", "2")
      new IntegerProperty() | 0            | null
      new LongProperty()    | 0            | null
      new DoubleProperty()  | 0            | null
      new FloatProperty()   | 0            | null
  }

  @Unroll("#allowableValues?.class.simpleName")
  def "adds enum values for parameters" () {
    when:
      maybeAddAllowableValuesToParameter(parameter, allowableValues)
    then:
    if (allowableValues instanceof AllowableListValues) {
      assert parameter.enum.size() == expectedSize
    } else if (allowableValues instanceof AllowableRangeValues) {
      assert parameter.minimum == safeBigDecimal(allowableValues.min)
      assert parameter.maximum == safeBigDecimal(allowableValues.max)
    } else {
      assert parameter.enum == null
    }
    where:
    parameter             | expectedSize | allowableValues
    new QueryParameter()  | 3            | new AllowableListValues(["A", "B", "C"], "List")
    new QueryParameter()  | 3            | new AllowableListValues(["1", "2", "3"], "List")
    new QueryParameter()  | 0            | new AllowableRangeValues("1", "2")
    new QueryParameter()  | 0            | new AllowableRangeValues("A", "B")
    new QueryParameter()  | 0            | null
  }

}
