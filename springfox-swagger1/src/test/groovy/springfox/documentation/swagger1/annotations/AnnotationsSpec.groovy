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

package springfox.documentation.swagger1.annotations

import spock.lang.Specification
import springfox.documentation.spring.web.dummy.controllers.ConcreteController
import springfox.documentation.swagger.annotations.Annotations

import java.lang.reflect.AnnotatedElement

import static springfox.documentation.swagger.annotations.Annotations.*

class AnnotationsSpec extends Specification {

  def "ApiResponses annotations should be looked up through the entire inheritance hierarchy"() {
    given:
      AnnotatedElement annotatedElement = ConcreteController.getMethod("get")
    expect:
      findApiResponsesAnnotations(annotatedElement).isPresent()
  }

  def "Cannot instantiate the annotations helper"() {
    when:
      new Annotations()
    then:
      thrown(UnsupportedOperationException)
  }

}
