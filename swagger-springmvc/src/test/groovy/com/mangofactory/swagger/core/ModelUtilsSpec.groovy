package com.mangofactory.swagger.core

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ModelUtilsSpec extends Specification {

   def "model types"() {
    expect:
      def type = ModelUtils.handlerReturnType(new TypeResolver(), handlerMethod)
      println "TYPE: $type"
      type.getErasedType() == expectedType
      ModelUtils.getModelName(new TypeResolver(), String.class)

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

}