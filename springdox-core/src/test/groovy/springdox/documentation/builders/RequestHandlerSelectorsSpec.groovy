package springdox.documentation.builders

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springdox.documentation.RequestHandler
import springdox.documentation.annotations.ApiIgnore

import static springdox.documentation.builders.RequestHandlerSelectors.*

class RequestHandlerSelectorsSpec extends Specification {
  def "Static types cannot be instantiated" () {
    when:
      RequestHandlerSelectors.newInstance();
    then:
      thrown(UnsupportedOperationException)
  }

  def "any predicate matches all RequestHandlers" () {
    expect:
      RequestHandlerSelectors.any().apply(Mock(RequestHandler))
  }

  def "none predicate matches no RequestHandlers" () {
    expect:
      !none().apply(Mock(RequestHandler))
  }

  def "withClassAnnotation predicate matches RequestHandlers with given Class Annotation" () {
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
    when:
      def handlerMethod = new HandlerMethod(clazz, methodName)
    then:
      withClassAnnotation(ApiIgnore).apply(new RequestHandler(reqMapping, handlerMethod)) == available
    where:
      clazz                   | methodName  | available
      new WithAnnotation()    | "test"      | true
      new WithoutAnnotation() | "test"      | false
  }

  def "withMethodAnnotation predicate matches RequestHandlers with given Class Annotation" () {
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
    when:
      def handlerMethod = new HandlerMethod(clazz, methodName)
    then:
      withMethodAnnotation(ApiIgnore).apply(new RequestHandler(reqMapping, handlerMethod)) == available
    where:
      clazz                   | methodName  | available
      new WithAnnotation()    | "test"      | true
      new WithoutAnnotation() | "test"      | false
  }

  @ApiIgnore
  public class WithAnnotation {
    @ApiIgnore
    public void test() {}
  }

  public class WithoutAnnotation {
    public void test() {}
  }
}
