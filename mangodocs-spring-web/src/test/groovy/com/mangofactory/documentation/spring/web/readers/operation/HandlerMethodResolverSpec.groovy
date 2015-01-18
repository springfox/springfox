package com.mangofactory.documentation.spring.web.readers.operation

import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedMethod
import com.mangofactory.documentation.spring.web.dummy.DummyClass
import com.mangofactory.documentation.spring.web.dummy.models.Example
import com.mangofactory.documentation.spring.web.dummy.models.Parent
import com.mangofactory.documentation.spring.web.mixins.HandlerMethodsSupport
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import spock.lang.Specification

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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

  def "When method was not resolvable calling methodParameters returns empty list" () {
    given:
      def methodResolver = new HandlerMethodResolver(new TypeResolver()) {
        @Override
        def ResolvedMethod getResolvedMethod(Method methodToResolve, Class<?> beanType) {
          return null;
        }
      }
    when:
      def parameters = methodResolver.methodParameters(handlerMethod)
    then:
      parameters.size() == 0
    where:
      handlerMethod << [methodWithChild(),  methodWithParent()]
  }

  def "When method was not resolvable calling methodReturnType returns return type as resolved" () {
    given:
      def methodResolver = new HandlerMethodResolver(new TypeResolver()) {
        @Override
        def ResolvedMethod getResolvedMethod(Method methodToResolve, Class<?> beanType) {
          return null;
        }
      }
    when:
      def returnType = methodResolver.methodReturnType(handlerMethod.method, handlerMethod.getBeanType())
    then:
      expectedReturnType.equals(returnType.erasedType.simpleName)
    where:
      handlerMethod     | expectedReturnType
      methodWithChild() | "void"
      methodWithParent()| "ResponseEntity"
  }

  def "When method was resolved with incorrect number of arguments it returns empty list" () {
    given:

      def methodResolver = new HandlerMethodResolver(new TypeResolver()) {
        @Override
        def ResolvedMethod getResolvedMethod(Method methodToResolve, Class<?> beanType) {
          return resolvedMethod();
        }
      }
    when:
      def parameters = methodResolver.methodParameters(handlerMethod)
    then:
      parameters.size() == 0
    where:
      handlerMethod << [methodWithChild(),  methodWithParent()]
  }

  def "Is able to determine if both types are void" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType = resolver.resolve(subClass)
    expect:
      methodResolver.bothAreVoids(resolvedType, superClass) == areSame
    where:
      superClass     | subClass     | areSame
      Void.TYPE      | Integer      | false
      Void.TYPE      | Void         | true
      Void.TYPE      | Void.TYPE    | true
      Void           | Integer      | false
      Void           | Void         | true
      Void           | Void.TYPE    | true
  }

  def "Is able to determine super classes" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType = resolver.resolve(subClass)
    expect:
      methodResolver.isSuperClass(resolvedType, superClass) == isSuperClass
    where:
      superClass          | subClass          | isSuperClass
      Parent              | DummyClass.Child  | false
      Parent              | Example           | true
      String              | Integer           | false
      Object              | String            | true
      Integer.TYPE        | Integer           | false
  }

  def "Is able to determine sub classes" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType = resolver.resolve(superClass)
    expect:
      methodResolver.isSubClass(resolvedType, subClass) == isSubClass
    where:
      superClass          | subClass          | isSubClass
      Parent              | DummyClass.Child  | false
      Parent              | Example           | true
      String              | Integer           | false
      Object              | String            | true
  }

  def "Is able to determine generic super classes" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType =  resolver.resolve(ChildResponseEntity, subClass)
      def superClassParameterized = parameterizedType(superClass, parameterizedType)
    expect:
      methodResolver.isGenericTypeSuperClass(resolvedType, superClassParameterized) == isSuperClass
    where:
      parameterizedType   | superClass          | subClass          | isSuperClass
      ResponseEntity      | Parent              | DummyClass.Child  | true
      ResponseEntity      | Parent              | Example           | true
      ResponseEntity      | String              | Integer           | true
      ResponseEntity      | Object              | String            | true
      ChildResponseEntity | Parent              | DummyClass.Child  | true
      ChildResponseEntity | Parent              | Example           | true
      ChildResponseEntity | String              | Integer           | true
      ChildResponseEntity | Object              | String            | true
      GenericType         | Parent              | DummyClass.Child  | false
      GenericType         | Parent              | Example           | false
      GenericType         | String              | Integer           | false
      GenericType         | Object              | String            | false
  }

  def "Is able to determine generic classes are covariant" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType =  resolver.resolve(ChildResponseEntity, subClass)
      def superClassParameterized = parameterizedType(superClass, parameterizedType)
    expect:
      methodResolver.covariant(resolvedType, superClassParameterized) == isSuperClass
    where:
      parameterizedType   | superClass          | subClass          | isSuperClass
      ResponseEntity      | Parent              | DummyClass.Child  | true
      ResponseEntity      | Parent              | Example           | true
      ResponseEntity      | String              | Integer           | true
      ResponseEntity      | Object              | String            | true
      ChildResponseEntity | Parent              | DummyClass.Child  | true
      ChildResponseEntity | Parent              | Example           | true
      ChildResponseEntity | String              | Integer           | true
      ChildResponseEntity | Object              | String            | true
      GenericType         | Parent              | DummyClass.Child  | false
      GenericType         | Parent              | Example           | false
      GenericType         | String              | Integer           | false
      GenericType         | Object              | String            | false
  }

  def "Is able to determine generic sub classes" () {
    given:
      def resolver = new TypeResolver()
      def methodResolver = new HandlerMethodResolver(resolver)
    and:
      def resolvedType =  resolver.resolve(ResponseEntity, superClass)
      def superClassParameterized = parameterizedType(subClass, parameterizedType)
    expect:
      methodResolver.isGenericTypeSubclass(resolvedType, superClassParameterized) == isSubclass
    where:
      parameterizedType   | superClass          | subClass          | isSubclass
      ResponseEntity      | Parent              | DummyClass.Child  | true
      ResponseEntity      | Parent              | Example           | true
      ResponseEntity      | String              | Integer           | true
      ResponseEntity      | Object              | String            | true
      ChildResponseEntity | Parent              | DummyClass.Child  | true
      ChildResponseEntity | Parent              | Example           | true
      ChildResponseEntity | String              | Integer           | true
      ChildResponseEntity | Object              | String            | true
      GenericType         | Parent              | DummyClass.Child  | false
      GenericType         | Parent              | Example           | false
      GenericType         | String              | Integer           | false
      GenericType         | Object              | String            | false
  }

  class ChildResponseEntity<T> extends ResponseEntity {

    ChildResponseEntity(HttpStatus statusCode) {
      super(statusCode)
    }

    ChildResponseEntity(Object body, HttpStatus statusCode) {
      super(body, statusCode)
    }

    ChildResponseEntity(MultiValueMap headers, HttpStatus statusCode) {
      super(headers, statusCode)
    }

    ChildResponseEntity(Object body, MultiValueMap headers, HttpStatus statusCode) {
      super(body, headers, statusCode)
    }
  }

  private ParameterizedType parameterizedType(Class typeArgument, Type parameterizedType) {
    new ParameterizedType() {
      @Override
      Type[] getActualTypeArguments() {
        [typeArgument]
      }

      @Override
      Type getRawType() {
       parameterizedType
      }

      @Override
      Type getOwnerType() {
        ResponseEntity
      }
    }
  }
}
