package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.RequestHandler
import springfox.documentation.RequestHandlerKey
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper

import java.util.stream.Stream

import static java.util.stream.Collectors.*
import static springfox.documentation.spring.web.paths.Paths.*

class DefaultRequestHandlerCombinerSpec extends Specification {
  def equality = new PathAndParametersEquivalence()

  @Unroll
  def "Combines request handlers effectively"() {
    given:
    def sut = new DefaultRequestHandlerCombiner()
    and:
    def input = [
        handler("/a", "a2", ["vendor/a"], param("a", String)),
        handler("/a", "a3", ["vendor/a", "vendor/b"], param("a", String)),
        handler("/a", "a1", ["vendor/a", "vendor/c"], param("a", String)),
        handler("/b", "b1", ["vendor/a"], param("b", String)),
        handler("/b", "b1", ["vendor/b"], param("b", String)),
        handler("/c", "c1a", ["vendor/c"], param("b", String)),
        handler("/c", "a1c", ["vendor/c"], param("c", String))
    ]
    def expected = [
        handler("/a", "a1", ["vendor/a", "vendor/b", "vendor/c"], param("a", String)),
        handler("/b", "b1", ["vendor/a", "vendor/b"], param("b", String)),
        handler("/c", "a1c", ["vendor/c"], param("c", String)),
        handler("/c", "c1a", ["vendor/c"], param("b", String))
    ]

    when:
    def combined = sut.combine(input)

    then:
    combined.size() == expected.size()
    expected.eachWithIndex { handler, index ->
      verifyAll {
        equality.test(handler, combined.get(index))
      }
    }
  }

  @Unroll
  def "Combines request handlers when there is only one"() {
    given:
    def sut = new DefaultRequestHandlerCombiner()

    and:
    def input = [handler("/a", "a", ["vendor/a"], param("a", String))]
    def expected = [handler("/a", "a", ["vendor/a"], param("a", String))]

    when:
    def combined = sut.combine(input)

    then:
    combined.size() == expected.size()
    expected.eachWithIndex { handler, index ->
      assert equality.test(handler, combined.get(index))
    }
  }

  @Unroll
  def "Combines request handlers when there is none"() {
    given:
    def sut = new DefaultRequestHandlerCombiner()

    and:
    def input = []
    def expected = []

    when:
    def combined = sut.combine(input)

    then:
    combined.size() == expected.size()
  }

  @Unroll
  def "Combines request handlers when input is null"() {
    given:
    def sut = new DefaultRequestHandlerCombiner()

    and:
    def input = null
    def expected = []

    when:
    def combined = sut.combine(input)

    then:
    combined.size() == expected.size()
  }

  RequestHandler handler(
      String path,
      String name,
      List<String> produces,
      ResolvedMethodParameter parameter) {
    def handler = Mock(RequestHandler)
    def key = Mock(RequestHandlerKey)
    handler.patternsCondition >> new WebMvcPatternsRequestConditionWrapper(
        ROOT,
        new PatternsRequestCondition(path))
    handler.getName() >> name
    handler.produces() >> Stream.of(produces).collect(toSet())
    handler.parameters >> [parameter]
    handler.supportedMethods() >> [RequestMethod.GET]
    handler.params() >> []
    handler.key() >> key
    key.toString() >> "mock of: " + path
    handler
  }

  def param(String name, Class<?> aClass, int index = 0) {
    new ResolvedMethodParameter(index, name, [], new TypeResolver().resolve(aClass))
  }
}


