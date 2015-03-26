package springfox.documentation

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

class RequestHandlerSpec extends Specification {
  def "tests getters and setters" (){
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
      RequestHandler sut = new RequestHandler(reqMapping, Mock(HandlerMethod))
    expect:
      sut.with {
        getRequestMapping()
        getHandlerMethod()
      }
  }
}
