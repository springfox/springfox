package springfox.documentation.spring.web.plugins

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.RequestHandler
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper
import springfox.documentation.spring.web.paths.Paths

import java.util.stream.Stream

import static java.util.stream.Collectors.*
import static org.springframework.web.bind.annotation.RequestMethod.*

class PathAndParametersEquivalenceSpec extends Specification {
  @Unroll
  def "two methods parameters are considered same => #areSame"() {
    given:
    def sut = new PathAndParametersEquivalence()

    expect:
    sut.test(first, second) == areSame
    (sut.doHash(first) == sut.doHash(second)) == sameHash

    where:
    first                                                | second                                                       | sameHash | areSame
    handler("/a", GET, ["vendor/a"], param("a", String)) | handler("/a", GET, ["vendor/a"], param("b", String))         | false    | false
    handler("/b", GET, ["vendor/a"], param("a", String)) | handler("/a", GET, ["vendor/a"], param("a", String))         | false    | false
    handler("/a", GET, ["vendor/a"], param("a", String)) | handler("/a", GET, ["vendor/b"], param("a", String))         | true     | true
    handler("/a", GET, ["vendor/a"], param("a", String)) | handler("/a", POST, ["vendor/a"], param("a", String))        | false    | false
    handler("/a", GET, ["vendor/a"], param("a", String)) | handler("/a", [GET, POST], ["vendor/a"], param("a", String)) | false    | true
    handler("/a", GET, ["vendor/a"], param("a", String)) | handler("/a", GET, ["vendor/a"], param("a", String))         | true     | true
    handlerWithDifferentParams("state=TX")               | handlerWithDifferentParams("state=CA")                       | false    | false
  }

  def handlerWithDifferentParams(String expression) {
    handler(
        "/a",
        [GET],
        ["vendor/a"],
        param("a", String),
        new ParamsRequestCondition(expression).expressions)
  }

  RequestHandler handler(
      String path,
      List<RequestMethod> methods,
      List<String> produces,
      ResolvedMethodParameter parameter,
      Set<NameValueExpression<String>> params) {
    def handler = Mock(RequestHandler)
    handler.patternsCondition >> new WebMvcPatternsRequestConditionWrapper(
        Paths.ROOT,
        new PatternsRequestCondition(path))
    handler.produces() >> Stream.of(produces).collect(toSet())
    handler.parameters >> [parameter]
    handler.supportedMethods() >> methods
    handler.params() >> params
    handler
  }

  RequestHandler handler(
      String path,
      List<RequestMethod> methods,
      List<String> produces,
      ResolvedMethodParameter parameter) {
    handler(path, methods, produces, parameter, [] as Set)
  }

  RequestHandler handler(
      String path,
      RequestMethod method,
      List<String> produces,
      ResolvedMethodParameter parameter) {
    handler(path, [method], produces, parameter)
  }

  def param(String name, Class<?> aClass, int index = 0) {
    new ResolvedMethodParameter(index, name, [], new TypeResolver().resolve(aClass))
  }
}
