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

package springfox.documentation.swagger.annotations
import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.controllers.ConcreteController

import java.lang.reflect.AnnotatedElement

import static springfox.documentation.swagger.annotations.Annotations.*

class AnnotationsSpec extends Specification {

  @Shared def resolver = new TypeResolver()
  @Shared def defaultType = resolver.resolve(String)

  def "ApiResponses annotations should be looked up through the entire inheritance hierarchy"() {
    given:
      AnnotatedElement annotatedElement = ConcreteController.getMethod("get", Object)
    expect:
      findApiResponsesAnnotations(annotatedElement).isPresent()
  }

  def "ApiParam annotations should be looked up through the entire inheritance hierarchy"() {
    given:
      AnnotatedElement annotatedElement = DummyClass.getMethod("annotatedWithApiParam")
    expect:
      findApiParamAnnotation(annotatedElement).isPresent()
  }

  def "Cannot instantiate the annotations helper"() {
    when:
      new Annotations()
    then:
      thrown(UnsupportedOperationException)
  }

  def "ResolvedType from ApiOperation annotation"() {
    given:
      def resolvedType = getResolvedType(apiOperation as ApiOperation, resolver, defaultType)

    expect:
      resolvedType == type
    where:
      apiOperation                                              | type
      null                                                      | defaultType
      [response: { -> Void}]                                    | defaultType
      [response: { -> String}, responseContainer: {-> "List"}]  | resolver.resolve(List, String)
      [response: { -> String}, responseContainer: {-> "Set"}]   | resolver.resolve(Set, String)
      [response: { -> String}, responseContainer: {-> "Other"}] | resolver.resolve(String)
      [response: { -> String}, responseContainer: { -> ""}]     | resolver.resolve(String)
  }

  def "ResolvedType from ApiResponse annotation"() {
    given:
    def resolvedType = getResolvedType(apiOperation as ApiResponse, resolver, defaultType)

    expect:
      resolvedType == type
    where:
      apiOperation                                              | type
      null                                                      | defaultType
      [response: { -> Void}]                                    | defaultType
      [response: { -> String}, responseContainer: {-> "List"}]  | resolver.resolve(List, String)
      [response: { -> String}, responseContainer: {-> "Set"}]   | resolver.resolve(Set, String)
      [response: { -> String}, responseContainer: {-> "Other"}] | resolver.resolve(String)
      [response: { -> String}, responseContainer: { -> ""}]     | resolver.resolve(String)
  }

}
