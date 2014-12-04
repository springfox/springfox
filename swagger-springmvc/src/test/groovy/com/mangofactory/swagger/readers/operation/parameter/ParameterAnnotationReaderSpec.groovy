package com.mangofactory.swagger.readers.operation.parameter

import com.google.common.base.Optional
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import spock.lang.Specification

import java.lang.reflect.Method

import static com.google.common.base.Strings.isNullOrEmpty

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
      ParameterAnnotationReader annotations = new ParameterAnnotationReader()
      Method method = A.class.methods.find { it.name.equals(methodName)}
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getMethod() >> method
      methodParameter.getParameterIndex() >> 0
    when:
      Optional<ApiParam> annotation = annotations.fromHierarchy(methodParameter, ApiParam.class)
    then:
      annotation.isPresent() == !isNullOrEmpty(expected)
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
