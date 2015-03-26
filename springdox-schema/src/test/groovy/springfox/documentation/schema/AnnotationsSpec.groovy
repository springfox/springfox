package springfox.documentation.schema

import com.fasterxml.jackson.annotation.JsonProperty
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
              mapper.deserializationConfig.introspect(TypeFactory.defaultInstance().constructType
                      (AnnotationType))
      BeanPropertyDefinition property = beanDesc.findProperties().find { it -> it.name == fieldName }

    expect:
      Annotations.findPropertyAnnotation(property, Autowired).isPresent() == isPresent
    where:
      fieldName                  | isPresent
      'field'                    | true
      'fieldWithGetter'          | true
      'fieldWithGetterAndSetter' | true
      'fieldWithNoAnnotations'   | false
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

    @JsonProperty
    private String fieldWithNoAnnotations


    @Autowired
    String getFieldWithGetter() {
      return fieldWithGetter
    }

    String getFieldWithGetterAndSetter() {
      return fieldWithGetterAndSetter
    }

    @Autowired
    void setFieldWithGetterAndSetter(String fieldWithGetterAndSetter) {
      this.fieldWithGetterAndSetter = fieldWithGetterAndSetter
    }
  }
}
