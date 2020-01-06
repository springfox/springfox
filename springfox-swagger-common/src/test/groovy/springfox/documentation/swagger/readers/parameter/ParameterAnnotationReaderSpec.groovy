/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger.readers.parameter

import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import spock.lang.Specification

import java.lang.reflect.Method

import static org.springframework.util.StringUtils.*

class ParameterAnnotationReaderSpec extends Specification {

  class A implements B, D {

    void method1(param) {}
    void method2(@ApiParam("param2") param) {}
    void method3(param) {}
    void method4(param) {}
    void method5(param) { }
  }

  interface B extends C{
    void method1(@ApiParam(name = "param1") param);
  }

  interface C {
    void method3(@ApiParam(name = "param3") param);
  }

  interface D extends E {
    void method4(@ApiParam(name = "param4")param);
  }

  interface E  {
    void method5(param);
  }

  def "Parameter annotations walk up the object interface hierarchy" () {
    given:
      Method method = A.class.methods.find { it.name.equals(methodName)}
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getMethod() >> method
      methodParameter.getParameterIndex() >> 0
    when:
      Optional<ApiParam> annotation = ParameterAnnotationReader.fromHierarchy(methodParameter, ApiParam.class)
    then:
      annotation.isPresent() == !isEmpty(expected)
      !annotation.isPresent() || annotation.get().name() == expected
    where:
      methodName  | expected
      "method1"   | "param1"
      "method2"   | ""
      "method3"   | "param3"
      "method4"   | "param4"
      "method5"   | ""
  }
}
