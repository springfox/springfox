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
      //Coverage
      strategy.getEndpointSuffix()
      strategy.getSkipPathCount()

    expect:
      strategy.getFirstGroupCompatibleName(requestMappingInfo, null) == group

    where:
      group                                         | path
      '/'                                           | '/'
      '/business'                                   | '/business'
      '/business'                                   | 'business'
      '/business/somethingElse'                     | '/business/somethingElse'
      '/business/somethingElse'                     | 'business/somethingElse'
      '/{businessId}'                               | '/{businessId}'
      '/{businessId}'                               | '/{businessId:\\d+}'
      '/{businessId}'                               | '/{businessId:\\w+}'
      '/business/{businessId}'                      | '/business/{businessId:\\d+}'
      '/business/{businessId}/accounts/{accountId}' | '/business/{businessId:\\d+}/accounts/{accountId:\\d+}'
   }

   @Unroll('Path: #path group: #group')
   def "controller to group names with skipped path elements "() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      DefaultControllerResourceNamingStrategy strategy = new DefaultControllerResourceNamingStrategy(skipPathCount: 2)

    expect:
      strategy.getGroupName(requestMappingInfo, null) == group

    where:
      group                                        | path
      'root'                                       | '/'
      'business'                                   | '/api/v1/business'
      'business'                                   | 'api/v1/business'
      'business_somethingElse'                     | '/api/v1/business/somethingElse'
      'business_somethingElse'                     | 'api/v1/business/somethingElse'
      '{businessId}'                               | '/api/v1/{businessId}'
      '{businessId}'                               | '/api/v1/{businessId:\\d+}'
      '{businessId}'                               | '/api/v1/{businessId:\\w+}'
      'business_{businessId}'                      | '/api/v1/business/{businessId:\\d+}'
      'business_{businessId}_accounts_{accountId}' | '/api/v1/business/{businessId:\\d+}/accounts/{accountId:\\d+}'
   }

   def "Controllers with and Api annotation should override the group name"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo(path)
      DefaultControllerResourceNamingStrategy strategy = new DefaultControllerResourceNamingStrategy(skipPathCount: 2)

    expect:
      strategy.getGroupName(requestMappingInfo, dummyControllerHandlerMethod()) == "Group name"

    where:
      path << ['/', '/api/v1/business', 'api/v1/business', '/api/v1/business/somethingElse']
   }

   def "longestCommonPath"() {
      DefaultControllerResourceNamingStrategy strategy = new DefaultControllerResourceNamingStrategy()

    expect:
      println paths
      strategy.longestCommonPath(paths) == expected

    where:
      paths << [
              ['/a/b/c', '/a/b/d', '/a/b/z'] as String[],
              ['/r/s/t', '/x/y/z', '/a/b/z'] as String[]
      ]
      expected << [
              '/a/b/',
              '/'
      ]
   }
}

