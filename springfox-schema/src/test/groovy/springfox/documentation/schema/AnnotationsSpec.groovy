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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Unroll

class AnnotationsSpec extends Specification {

  def "Cannot instantiate the annotations helper"() {
    when:
      new Annotations()
    then:
      thrown(UnsupportedOperationException)
  }

  @Unroll
  def "Introspects bean annotations on deserializable properties"() {
    given:
      ObjectMapper mapper = new ObjectMapper()
      BeanDescription beanDesc =
              mapper.deserializationConfig.introspect(TypeFactory.defaultInstance()
                  .constructType(AnnotationType))
      BeanPropertyDefinition property = beanDesc.findProperties().find { it -> it.name == fieldName }

    expect:
      Annotations.findPropertyAnnotation(property, Autowired).isPresent() == isPresent
    where:
      fieldName                  | isPresent
      'field'                    | true
      'fieldWithGetter'          | true
      'fieldWithGetterAndSetter' | true
      'fieldWithNoAnnotations'   | false
      'fieldWith2Setters'        | true
  }

  @Unroll
  def "Introspects bean annotations on serializable properties"() {
    given:
      ObjectMapper mapper = new ObjectMapper()
      BeanDescription beanDesc =
              mapper.serializationConfig.introspect(TypeFactory.defaultInstance()
                      .constructType(AnnotationType))
      BeanPropertyDefinition property = beanDesc.findProperties().find { it -> it.name == fieldName }
    expect:
      Annotations.findPropertyAnnotation(property, Autowired).isPresent() == isPresent
    where:
      fieldName                  | isPresent
      'field'                    | true
      'fieldWithGetter'          | true
      'fieldWithGetterAndSetter' | true
      'fieldWithNoAnnotations'   | false
      'fieldWith2Setters'        | true
  }

  @Unroll
  def "Introspects bean annotations and gets the member name"() {
    given:
      ObjectMapper mapper = new ObjectMapper()
      BeanDescription beanDesc =
              mapper.serializationConfig.introspect(TypeFactory.defaultInstance()
                      .constructType(AnnotationType))
      BeanPropertyDefinition property = beanDesc.findProperties().find { it -> it.name == fieldName }
    expect:
      Annotations.memberName(property.getPrimaryMember()) == memberName
    where:
      fieldName                  | memberName
      'field'                    | 'field'
      'fieldWithGetter'          | 'getFieldWithGetter'
      'fieldWithGetterAndSetter' | 'getFieldWithGetterAndSetter'
      'fieldWithNoAnnotations'   | 'fieldWithNoAnnotations'
      'fieldWith2Setters'        | 'getFieldWith2Setters'
  }

  def "Member unwrapped returns valid result"() {
    given:
      ObjectMapper mapper = new ObjectMapper()
      BeanDescription beanDesc =
          mapper.serializationConfig.introspect(TypeFactory.defaultInstance()
              .constructType(AnnotationType))
      BeanPropertyDefinition property = beanDesc.findProperties().find { it -> it.name == fieldName }
    expect:
      Annotations.memberIsUnwrapped(property?.getPrimaryMember()) == isUnwrapped
    where:
      fieldName                  | isUnwrapped
      'fieldUnwrapped'           | true
      null                       | false
  }

  def "when member is null"() {
    expect:
      Annotations.memberName(null) == ""
  }

  def "when member.getMember is null"() {
    given:
      def member = Mock(AnnotatedMember)
    and:
      member.getMember() >> null
    expect:
      Annotations.memberName(member) == ""
  }

  class AnnotationType {
    @Autowired
    @JsonProperty
    private String field
    private String fieldWithGetter
    private String fieldWithGetterAndSetter
    private String fieldWith2Setters;
    @JsonUnwrapped
    private Category fieldUnwrapped;

    @JsonProperty
    private String fieldWithNoAnnotations


    @Autowired
    String getFieldWithGetter() {
      return fieldWithGetter
    }

    String getFieldWithGetterAndSetter() {
      return fieldWithGetterAndSetter
    }

    public String getFieldWith2Setters() {
      return fieldWith2Setters
    }

    @JsonIgnore
    public void setFieldWith2Setters(UUID fieldWith2Setters) {
      this.fieldWith2Setters = ownerId.toString();
    }

    @JsonProperty
    @Autowired
    public void setFieldWith2Setters(String fieldWith2Setters) {
      this.fieldWith2Setters = fieldWith2Setters;
    }

    @Autowired
    void setFieldWithGetterAndSetter(String fieldWithGetterAndSetter) {
      this.fieldWithGetterAndSetter = fieldWithGetterAndSetter
    }

  }
}
