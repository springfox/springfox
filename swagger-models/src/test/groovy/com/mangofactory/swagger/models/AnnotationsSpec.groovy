package com.mangofactory.swagger.models

import spock.lang.Specification

import javax.swing.text.html.Option
import java.lang.reflect.AnnotatedElement

class AnnotationsSpec extends Specification {

  def "ApiResponses annotations should be looked up through the entire inheritance hierarchy"() {
    given:
      AnnotatedElement annotatedElement = ServiceWithAnnotationOnInterface.SimpleServiceImpl.getMethod("aMethod")
    expect:
      Annotations.findApiResponsesAnnotations(annotatedElement).isPresent() == true
  }

  def "ResponseStatus annotation should be looked up through the entire inheritance hierarchy"(){
    given:
      AnnotatedElement annotatedElement = ServiceWithAnnotationOnInterface.SimpleServiceImpl.getMethod("aMethod")
    expect:
      Annotations.findResponseStatusAnnotation(annotatedElement).isPresent() == true
  }

}
