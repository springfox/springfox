package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.ResolvedMethodParameter

class ResolvedMethodParameterEquivalenceSpec extends Specification {
  @Unroll
  def "two methods parameters are considered same => #areSame" (){
    given:
      def sut = new ResolvedMethodParameterEquivalence()
    expect:
      sut.test(first, second) == areSame
      (sut.doHash(first) == sut.doHash(second)) == areSame
    where:
      first                   | second                | areSame
      param("a", String)      | param("b", String)    | false
      param("a", String)      | param("a", String, 2) | false
      param("a", Integer)     | param("a", String)    | false
      param("a", String, 2)   | param("a", String, 2) | true

  }

  def param(String name, Class<?> aClass, int index = 0) {
    new ResolvedMethodParameter(index, name, [], new TypeResolver().resolve(aClass))
  }
}
