package com.mangofactory.swagger.core

import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Method

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class DefaultControllerResourceGroupingStrategySpec extends Specification {

   def "should name based on reflective method"() {
    given:
      RequestMappingInfo requestMappingInfo = null
      HandlerMethod handlerMethod = dummyHandlerMethod()
      DefaultControllerResourceGroupingStrategy defaultControllerResourceGroupingStrategy =
         new DefaultControllerResourceGroupingStrategy()
    expect:
     "dummyMethod" == defaultControllerResourceGroupingStrategy.getControllerName(requestMappingInfo, handlerMethod)
   }

   @Unroll('Path: #path group: #group')
   def "controller to group names "() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo([path])
      DefaultControllerResourceGroupingStrategy strategy = new DefaultControllerResourceGroupingStrategy()

    expect:
      group == strategy.getGroupCompatibleName(requestMappingInfo, null)

    where:
      group                                        | path
      'business'                                   | '/business'
      'business'                                   | 'business'
      'business/somethingElse'                     | '/business/somethingElse'
      'business/somethingElse'                     | 'business/somethingElse'
      '(businessId)'                               | '/{businessId}'
      '(businessId)'                               | '/{businessId:\\d+}'
      '(businessId)'                               | '/{businessId:\\w+}'
      'business/(businessId)'                      | '/business/{businessId:\\d+}'
      'business/(businessId)/accounts/(accountId)' | '/business/{businessId:\\d+}/accounts/{accountId:\\d+}'
   }
}
