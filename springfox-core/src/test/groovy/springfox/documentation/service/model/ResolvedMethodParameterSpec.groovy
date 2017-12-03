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

package springfox.documentation.service.model

import com.fasterxml.classmate.TypeResolver
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import spock.lang.Specification
import springfox.documentation.service.ResolvedMethodParameter

class ResolvedMethodParameterSpec extends Specification {
  def "Bean properties test" () {
    given:
      def resolved = new TypeResolver().resolve(String)
      def methodParameter = Mock(MethodParameter)
    and:
      methodParameter.parameterIndex >> 1
      methodParameter.parameterAnnotations >> []
    when:
      def sut = new ResolvedMethodParameter("defaultName", methodParameter, resolved)
    then:
      sut.parameterIndex == 1
      !sut.hasParameterAnnotations()
      sut.parameterType == resolved
      sut.defaultName().orElse(null) == "defaultName"
  }

  def "Finds annotations" () {
    given:
      def resolved = new TypeResolver().resolve(String)
      def methodParameter = Mock(MethodParameter)
    and:
      methodParameter.parameterIndex >> 1
      methodParameter.parameterAnnotations >> [[required: { -> true }] as RequestParam]
    when:
      def sut = new ResolvedMethodParameter("defaultName", methodParameter, resolved)
    then:
      sut.parameterIndex == 1
      sut.hasParameterAnnotations()
      sut.hasParameterAnnotation(RequestParam)
      sut.findAnnotation(RequestParam).isPresent()
    and:
      !sut.hasParameterAnnotation(PathVariable)
      !sut.findAnnotation(PathVariable).isPresent()
  }

  def "Replace types" () {
    given:
      def resolved = new TypeResolver().resolve(String)
      def replaced = new TypeResolver().resolve(Integer)
      def methodParameter = Mock(MethodParameter)
    and:
      methodParameter.parameterIndex >> 1
      methodParameter.parameterAnnotations >> []
    and:
      def sut = new ResolvedMethodParameter("defaultName", methodParameter, resolved)
    when:
      def sutReplaced = sut.replaceResolvedParameterType(replaced)
    then:
      sut.parameterIndex == 1
      sut.parameterType == resolved
      sutReplaced.parameterType == replaced
      sut.defaultName().orElse(null) == "defaultName"
  }

  def "Adding extra annotations" () {
    given:
      def resolved = new TypeResolver().resolve(String)
      def methodParameter = Mock(MethodParameter)
    and:
      methodParameter.parameterIndex >> 1
      methodParameter.parameterAnnotations >> []
    when:
      def sut = new ResolvedMethodParameter("defaultName", methodParameter, resolved)
      def sutAnnotated = sut.annotate([required: { -> true }] as RequestBody)
    then:
      !sut.hasParameterAnnotations()
      !sut.hasParameterAnnotation(RequestBody)
      !sut.findAnnotation(RequestBody).isPresent()
    and:
      sutAnnotated.hasParameterAnnotations()
      sutAnnotated.hasParameterAnnotation(RequestBody)
      sutAnnotated.findAnnotation(RequestBody).isPresent()
  }
}
