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

package springfox.documentation.builders

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import springfox.documentation.RequestHandler
import springfox.documentation.annotations.ApiIgnore
import springfox.documentation.schema.JustAnotherClass

import static springfox.documentation.builders.RequestHandlerSelectors.*

class RequestHandlerSelectorsSpec extends Specification {
  def "Static types cannot be instantiated" () {
    when:
      RequestHandlerSelectors.newInstance();
    then:
      thrown(UnsupportedOperationException)
  }

  def "any predicate matches all RequestHandlers" () {
    expect:
      RequestHandlerSelectors.any().test(Mock(RequestHandler))
  }

  def "none predicate matches no RequestHandlers" () {
    expect:
      !none().test(Mock(RequestHandler))
  }

  def "withClassAnnotation predicate matches RequestHandlers with given Class Annotation" () {
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
    when:
      def handlerMethod = new HandlerMethod(clazz, methodName)
    then:
      withClassAnnotation(ApiIgnore).test(new MockRequestHandler(reqMapping, handlerMethod)) == available
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
      withMethodAnnotation(ApiIgnore).test(new MockRequestHandler(reqMapping, handlerMethod)) == available
    where:
      clazz                   | methodName  | available
      new WithAnnotation()    | "test"      | true
      new WithoutAnnotation() | "test"      | false
  }

  def "basePackage predicate matches RequestHandlers with given Class Annotation" () {
    given:
      def reqMapping = new RequestMappingInfo(null,null,null,null,null,null, null)
    when:
      def handlerMethod = new HandlerMethod(clazz, methodName)
    then:
      basePackage("springfox.documentation.builders")
        .test(new MockRequestHandler(reqMapping, handlerMethod)) == available
    where:
      clazz                   | methodName  | available
      new WithAnnotation()    | "test"      | true
      new WithoutAnnotation() | "test"      | true
      new JustAnotherClass()  | "name"      | false
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
