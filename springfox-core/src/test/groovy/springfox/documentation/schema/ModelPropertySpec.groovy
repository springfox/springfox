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
      def args = [
          "Test", 
           resolver.resolve(String), 
           "qType1", 
           0, 
           true, 
           true, 
           true, 
           "desc", 
           new AllowableRangeValues("1","5"), 
           "example",
           "pattern"]

      ModelProperty property = Spy(ModelProperty, constructorArgs: args);
      
      def testArgs = [
          name, 
          resolver.resolve(type), 
          qualifiedType, 
          position, 
          required, 
          isHidden, 
          readOnly, 
          description, 
          new AllowableRangeValues(allowableMin,allowableMax), 
          example, 
          pattern]
      
      ModelProperty testProperty = Spy(ModelProperty, constructorArgs: testArgs);
    and:
      property.getModelRef() >> new ModelRef("string", null, true)
      testProperty.getModelRef() >> new ModelRef(refType, refModel, refIsMap)
    expect:
      property.equals(testProperty) == expectedEquality
      property.equals(property)
      !property.equals(null)
      !property.equals(new Object())
    and:
      (property.hashCode() == testProperty.hashCode()) == expectedEquality
      property.hashCode() == property.hashCode()
    where:
      name << ["Test", "Test", "Test1", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test", "Test"]
      type << [String, String, String, Integer, String, String, String, String, String, String, String, String, String]
      qualifiedType << ["qType1", "qType1", "qType1", "qType2", "qType1", "qType1", "qType1", "qType1", "qType1", "qType2", "qType1", "qType1", "qType1"]
      position << [0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 3, 0]
      required << [true, true, true, false, true, false, true, true, true, true, true, false, true]
      isHidden << [true, true, true, false, false, true, true, true, true, true, true, true, true]
      readOnly << [true, true, true, false, true, false, true, true, true, true, false, true, true]
      description << ["desc", "desc", "desc", "desc1", "desc", "desc", "desc", "desc", "desc1", "desc", "desc1", "desc1", "desc"]
      allowableMin << ["1", "1", "1", "1", "1", "1", "1", "1", "1", "5", "1", "1", "1"]
      allowableMax << ["5", "3", "3", "9", "9", "9", "5", "5", "5", "9", "5", "5", "5"]
      refType << ["string", "string", "string", "string", "string", "string", "string", "string", "integer", "string", "string", "string", "integer"]
      refModel << [null, null, null, null, null, null, null, null, null, null, null, null, null]
      refIsMap << [true, true, true, false, false, false, true, true, true, true, true, true, true]
      example << ["example", "example", "example", "example1", "example", "example", "example", "example1", "example", "example", "example", "example", "example"]
      pattern << ["pattern", "pattern", "pattern", "pattern1", "pattern", "pattern", "pattern1", "pattern", "pattern", "pattern", "pattern", "pattern", "pattern"]
      expectedEquality << [true, false, false, false, false, false, false, false, false, false, false, false, false]
  }
}
