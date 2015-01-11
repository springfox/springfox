package com.mangofactory.swagger.core

import com.fasterxml.classmate.GenericType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.type.SimpleType
import com.mangofactory.schema.ResolvedTypes
import com.mangofactory.spring.web.HandlerMethodReturnTypes
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

import static com.mangofactory.schema.ResolvedTypes.asResolved

@Mixin(RequestMappingSupport)
class ModelUtilsSpec extends Specification {

   def "model types"() {
    expect:
      def type = HandlerMethodReturnTypes.handlerReturnType(new TypeResolver(), handlerMethod)
      println "TYPE: $type"
      type.getErasedType() == expectedType
      ResolvedTypes.typeName(asResolved(new TypeResolver(), String.class))

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

  def "Get response class name from ResolvedType"(){
    expect:
      def modelResponseClass = ResolvedTypes.responseTypeName(new TypeResolver().resolve(GenericType, clazz))
      modelResponseClass == expectedResponseClassName

    where:
      clazz       | expectedResponseClassName
      SimpleType  | "GenericType«SimpleType»"
      Integer     | "GenericType«int»"
  }

}