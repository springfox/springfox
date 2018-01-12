/*
 *
 *  Copyright 2015-2017 the original author or authors.
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

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.service.AllowableValues;

class ModelPropertySpec extends Specification {
  @Shared
  def TypeResolver resolver = new TypeResolver()

  def "ModelProperty .equals and .hashCode works as expected"() {
    given:
      def args = ["Test", resolver.resolve(String), "qT1", 0, true, true, true, "desc",
                  new AllowableRangeValues("1","5"), "exmp", "pttn", new ArrayList<>()]

      ModelProperty property = Spy(ModelProperty, constructorArgs: args);
      
      def testArgs = [name, resolver.resolve(type), qualifiedType, position, required, 
                     isHidden, readOnly, description, new AllowableRangeValues(allowableMin,allowableMax), 
                     example,  pattern, new ArrayList<>()]
      
      ModelProperty testProperty = Spy(ModelProperty, constructorArgs: testArgs);
    and:
      property.modelRef >> new ModelRef("string", null, true)
      testProperty.modelRef >> new ModelRef(refType, refModel, refIsMap)
    expect:
      property.equals(testProperty) == expectedEquality
      property.equals(property)
      !property.equals(null)
      !property.equals(new Object())
    and:
      (property.hashCode() == testProperty.hashCode()) == expectedEquality
      property.hashCode() == property.hashCode()
    where:
      name << ["Test", "Test1", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test"]
      type << [String, String, Integer, String, String, String, String, String, String, String, String, String, String]
      qualifiedType << ["qT1", "qT1", "qT1", "qT2", "qT1", "qT1", "qT1", "qT1", "qT1", "qT1", "qT1", "qT1", "qT1"]
      position << [0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0]
      required << [true, true, true, true, true, false, true, true, true, true, true, true, true]
      isHidden << [true, true, true, true, true, true, false, true, true, true, true, true, true]
      readOnly << [true, true, true, true, true, true, true, false, true, true, true, true, true]
      description << ["desc", "desc", "desc", "desc", "desc", "desc", "desc", "desc", "desc1", "desc", "desc", "desc", "desc"]
      allowableMin << ["1", "1", "1", "1", "1", "1", "1", "1", "1", "5", "1", "1", "1"]
      allowableMax << ["5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5"]
      refType << ["string", "T", "T", "T", "T", "T", "T", "T", "T", "T", "T", "string", "string"]
      refModel << [null, null, null, null, null, null, null, null, null, null, null, null, null]
      refIsMap << [true, true, true, true, true, true, true, true, true, true, false, true, true]
      example << ["exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp", "exmp1", "exmp"]
      pattern << ["pttn", "pttn", "pttn", "pttn1", "pttn", "pttn", "pttn", "pttn", "pttn", "pttn", "pttn", "pttn", "pttn1"]
      expectedEquality << [true, false, false, false, false, false, false, false, false, false, true, false, false]
  }
}
