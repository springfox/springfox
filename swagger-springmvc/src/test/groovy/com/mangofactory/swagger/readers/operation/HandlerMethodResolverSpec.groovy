package com.mangofactory.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.HandlerMethodsSupport
import spock.lang.Specification

@Mixin(HandlerMethodsSupport)
class HandlerMethodResolverSpec extends Specification {
  def "Methods with same name are distinguished based on variance of parameters and return types" () {
    given:
      def methodResolver = new HandlerMethodResolver(new TypeResolver())
      def resolvedParameters = methodResolver.methodParameters(handlerMethod)
              .collect() { it.resolvedParameterType.getErasedType().simpleName }
              .sort()
      def resolvedReturnType = methodResolver.methodReturnType(handlerMethod.method, handlerMethod.getBeanType()).erasedType.simpleName
    expect:
      parameters == resolvedParameters
      returnType == resolvedReturnType
    where:
      handlerMethod     | returnType      | parameters
      methodWithChild() | "Void"          | ["Child", "Integer"]
      methodWithParent()| "ResponseEntity"| ["Integer", "Parent"]
  }
}
