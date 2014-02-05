package com.mangofactory.swagger.core

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class DefaultControllerResourceNamingStrategySpec extends Specification {

   @Unroll('Path: #path group: #group')
   def "controller to group names "() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      DefaultControllerResourceNamingStrategy strategy = new DefaultControllerResourceNamingStrategy()

    expect:
      group == strategy.getFirstGroupCompatibleName(requestMappingInfo, null)

    where:
      group                                        | path
      'root'                                       | '/'
      'business'                                   | '/business'
      'business'                                   | 'business'
      'business/somethingElse'                     | '/business/somethingElse'
      'business/somethingElse'                     | 'business/somethingElse'
      '{businessId}'                               | '/{businessId}'
      '{businessId}'                               | '/{businessId:\\d+}'
      '{businessId}'                               | '/{businessId:\\w+}'
      'business/{businessId}'                      | '/business/{businessId:\\d+}'
      'business/{businessId}/accounts/{accountId}' | '/business/{businessId:\\d+}/accounts/{accountId:\\d+}'
   }


   @Unroll('Path: #path group: #group')
   def "controller to group names with skipped path elements "() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      DefaultControllerResourceNamingStrategy strategy = new DefaultControllerResourceNamingStrategy(skipPathCount: 2)

    expect:
      strategy.getGroupName(requestMappingInfo, null) == group

    where:
      group          | path
      'root'         | '/'
      'business'     | '/api/v1/business'
      'business'     | 'api/v1/business'
      'business'     | '/api/v1/business/somethingElse'
      'business'     | 'api/v1/business/somethingElse'
      '{businessId}' | '/api/v1/{businessId}'
      '{businessId}' | '/api/v1/{businessId:\\d+}'
      '{businessId}' | '/api/v1/{businessId:\\w+}'
      'business'     | '/api/v1/business/{businessId:\\d+}'
      'business'     | '/api/v1/business/{businessId:\\d+}/accounts/{accountId:\\d+}'
   }


}
