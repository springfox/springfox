package com.mangofactory.swagger.core

import com.mangofactory.swagger.dummy.DummyModels
import com.mangofactory.swagger.mixins.RequestMappingSupport
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ModelUtilsSpec extends Specification {

   def "model types"() {
    expect:
      def type = ModelUtils.getHandlerReturnType(handlerMethod)
      println "TYPE: $type"
      type == expectedType

    where:
      handlerMethod                                            | expectedType
      dummyHandlerMethod("methodWithConcreteResponseBody")     | DummyModels.BusinessModel.class
      dummyHandlerMethod("methodWithConcreteCorporationModel") | DummyModels.CorporationModel.class
   }

}