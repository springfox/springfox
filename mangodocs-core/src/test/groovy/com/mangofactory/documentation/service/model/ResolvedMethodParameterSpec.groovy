package com.mangofactory.documentation.service.model

import com.fasterxml.classmate.TypeResolver
import org.springframework.core.MethodParameter
import spock.lang.Specification

class ResolvedMethodParameterSpec extends Specification {
  def "Bean properties test" () {
    given:
      def resolved = new TypeResolver().resolve(String)
      def methodParameter = Mock(MethodParameter)
    when:
      def sut = new ResolvedMethodParameter(methodParameter, resolved)
    then:
      sut.methodParameter == methodParameter
      sut.resolvedParameterType == resolved
  }
}
