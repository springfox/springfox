package com.mangofactory.swagger.annotations
import com.mangofactory.swagger.dummy.controllers.ConcreteController
import spock.lang.Specification

import java.lang.reflect.AnnotatedElement

import static com.mangofactory.swagger.annotations.Annotations.*

class AnnotationsSpec extends Specification {

  def "ApiResponses annotations should be looked up through the entire inheritance hierarchy"() {
    given:
      AnnotatedElement annotatedElement = ConcreteController.getMethod("get")
    expect:
      findApiResponsesAnnotations(annotatedElement).isPresent()
  }

  def "Cannot instantiate the annotations helper" () {
    when:
      new Annotations()
    then:
      thrown(UnsupportedOperationException)
  }

}
