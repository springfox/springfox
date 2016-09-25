/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

class RequestHandlerKeySpec extends Specification {
  def "tests getters"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_ATOM_XML] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

    expect:
        first.getPathMappings().size() == 2
        first.getSupportedMediaTypes().size() == 2
        first.getProducibleMediaTypes().size() == 2
        first.getSupportedMethods().size() == 2
  }

  def "same request handler keys are equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_ATOM_XML] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = first
    expect:
      first.equals(second)
  }

  def "equal request handler keys are equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_ATOM_XML] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = new RequestHandlerKey(
          ["/b", "/a"] as Set,
          [RequestMethod.GET, RequestMethod.PATCH] as Set,
          [MediaType.APPLICATION_ATOM_XML, MediaType.ALL_VALUE] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_JSON] as Set,
      )
    expect:
      first.equals(second)
      first.hashCode() == second.hashCode()
  }

  def "detects paths are not equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_ATOM_XML] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = new RequestHandlerKey(
          ["/b", "/a", "/c"] as Set,
          [RequestMethod.GET, RequestMethod.PATCH] as Set,
          [MediaType.APPLICATION_ATOM_XML, MediaType.ALL_VALUE] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_JSON] as Set,
      )
    expect:
      !first.equals(second)
  }

  def "detects methods are not equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_ATOM_XML] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = new RequestHandlerKey(
          ["/b", "/a",] as Set,
          [RequestMethod.POST, RequestMethod.PATCH] as Set,
          [MediaType.APPLICATION_ATOM_XML, MediaType.ALL_VALUE] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_JSON] as Set,
      )
    expect:
      !first.equals(second)
  }

  def "detects produces are not equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = new RequestHandlerKey(
          ["/b", "/a",] as Set,
          [RequestMethod.GET, RequestMethod.PATCH] as Set,
          [MediaType.APPLICATION_ATOM_XML, MediaType.ALL_VALUE] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_JSON] as Set,
      )
    expect:
      !first.equals(second)
  }

  def "detects consumes are not equal"() {
    given:
      def first = new RequestHandlerKey(
          ["/a", "/b"] as Set,
          [RequestMethod.PATCH, RequestMethod.GET] as Set,
          [MediaType.ALL_VALUE] as Set,
          [MediaType.APPLICATION_JSON, MediaType.ALL_VALUE] as Set,
      )

      def second = new RequestHandlerKey(
          ["/b", "/a",] as Set,
          [RequestMethod.GET, RequestMethod.PATCH] as Set,
          [MediaType.APPLICATION_ATOM_XML, MediaType.ALL_VALUE] as Set,
          [MediaType.ALL_VALUE, MediaType.APPLICATION_JSON] as Set,
      )
    expect:
      !first.equals(second)
  }
}
