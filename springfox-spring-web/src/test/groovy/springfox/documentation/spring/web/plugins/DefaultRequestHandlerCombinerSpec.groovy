package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.RequestHandler
import springfox.documentation.service.ResolvedMethodParameter

import static com.google.common.collect.Sets.newHashSet

class DefaultRequestHandlerCombinerSpec extends Specification {
  def equality = new PathAndParametersEquivalence()

  @Unroll
  def "Combines request handlers effectively" () {
    given:
      def sut = new DefaultRequestHandlerCombiner()
    and:
     def input  = [
        handler("/a", ["vendor/a"], param("a", String)),
        handler("/a", ["vendor/a", "vendor/b"], param("a", String)),
        handler("/a", ["vendor/a", "vendor/c"], param("a", String)),
        handler("/b", ["vendor/a"], param("b", String)),
        handler("/b", ["vendor/b"], param("b", String)),
        handler("/c", ["vendor/c"], param("b", String)),
        handler("/c", ["vendor/c"], param("c", String))
      ]
      def expected = [
          handler("/a", ["vendor/a", "vendor/b", "vendor/c"], param("a", String)),
          handler("/b", ["vendor/a", "vendor/b"], param("b", String)),
          handler("/c", ["vendor/c"], param("b", String)),
          handler("/c", ["vendor/c"], param("c", String))
      ]
    when:
      def combined = sut.combine(input)
    then:
      combined.size() == expected.size()
      expected.eachWithIndex { handler, index ->
          assert equality.equivalent(handler, combined.get(index))
       }
  }


  @Unroll
  def "Combines request handlers when there is only one" () {
    given:
      def sut = new DefaultRequestHandlerCombiner()
    and:
      def input  = [ handler("/a", ["vendor/a"], param("a", String)) ]
      def expected = [ handler("/a", ["vendor/a"], param("a", String)) ]
    when:
      def combined = sut.combine(input)
    then:
      combined.size() == expected.size()
      expected.eachWithIndex { handler, index ->
        assert equality.equivalent(handler, combined.get(index))
      }
  }

  @Unroll
  def "Combines request handlers when there is none" () {
    given:
      def sut = new DefaultRequestHandlerCombiner()
    and:
      def input  = []
      def expected = []
    when:
      def combined = sut.combine(input)
    then:
      combined.size() == expected.size()
  }

  @Unroll
  def "Combines request handlers when input is null" () {
    given:
      def sut = new DefaultRequestHandlerCombiner()
    and:
      def input  = null
      def expected = []
    when:
      def combined = sut.combine(input)
    then:
      combined.size() == expected.size()
  }

  RequestHandler handler(
      String path,
      List<String> produces,
      ResolvedMethodParameter parameter) {
    def handler = Mock(RequestHandler)
    handler.patternsCondition >> new PatternsRequestCondition(path)
    handler.produces() >> newHashSet(produces)
    handler.parameters >> [parameter]
    handler.supportedMethods() >> [RequestMethod.GET]
    handler.params() >> []
    handler
  }

  def param(String name, Class<?> aClass, int index = 0) {
    new ResolvedMethodParameter(index, name, [], new TypeResolver().resolve(aClass))
  }
}


