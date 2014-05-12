package com.mangofactory.swagger.plugin

import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.core.SwaggerApiResourceListing
import com.mangofactory.swagger.core.SwaggerCache
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner
import com.wordnik.swagger.model.AuthorizationType
import com.wordnik.swagger.model.ResponseMessage
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletRequest

import static com.mangofactory.swagger.ScalaUtils.toOption
import static com.mangofactory.swagger.models.alternates.Alternates.newMapRule
import static org.springframework.http.HttpStatus.OK
import static org.springframework.web.bind.annotation.RequestMethod.*

class SwaggerSpringMvcPluginSpec extends Specification {

  SpringSwaggerConfig springSwaggerConfig
  SwaggerSpringMvcPlugin plugin

  void setup() {
    springSwaggerConfig = new SpringSwaggerConfig()
    plugin = new SwaggerSpringMvcPlugin(springSwaggerConfig)
  }

  def "Should have sensible defaults when built with minimal configuration"() {
    when:
      plugin.build()

    then:
      plugin.swaggerGroup == 'default'
      plugin.includePatterns == [".*?"]
      plugin.authorizationTypes == null

      plugin.apiInfo.title() == 'default Title'
      plugin.apiInfo.description() == 'Api Description'
      plugin.apiInfo.termsOfServiceUrl() == 'Api terms of service'
      plugin.apiInfo.contact() == 'Contact Email'
      plugin.apiInfo.license() == 'Licence Type'
      plugin.apiInfo.licenseUrl() == 'License URL'

      plugin.excludeAnnotations == []
      plugin.resourceGroupingStrategy instanceof ClassOrApiAnnotationResourceGrouping
      plugin.swaggerPathProvider instanceof RelativeSwaggerPathProvider
      plugin.alternateTypeProvider instanceof AlternateTypeProvider
  }

  def "Swagger global response messages should override the default for a particular RequestMethod"() {
    when:
      plugin.globalResponseMessage(RequestMethod.GET, [new ResponseMessage(OK.value(), "blah", toOption(null))])
            .build()

    then:
      SwaggerGlobalSettings swaggerGlobalSettings = plugin.swaggerGlobalSettings
      swaggerGlobalSettings.getGlobalResponseMessages()[RequestMethod.GET][0].message() == "blah"
      swaggerGlobalSettings.getGlobalResponseMessages()[RequestMethod.GET].size() == 1

    and: "defaults are preserved"
      swaggerGlobalSettings.getGlobalResponseMessages().keySet().containsAll(
            [POST, PUT, DELETE, PATCH, TRACE, OPTIONS, HEAD]
      )

  }

  def "Swagger ignorableParameterTypes should append to the default ignorableParameterTypes"() {
    when:
      plugin
            .ignoredParameterTypes(AbstractSingletonProxyFactoryBean.class, ProxyFactoryBean.class)
            .build()

    then:
      SwaggerGlobalSettings swaggerGlobalSettings = plugin.swaggerGlobalSettings
      swaggerGlobalSettings.getIgnorableParameterTypes().contains(AbstractSingletonProxyFactoryBean.class)
      swaggerGlobalSettings.getIgnorableParameterTypes().contains(ProxyFactoryBean.class)

    and: "one of the defaults"
      swaggerGlobalSettings.getIgnorableParameterTypes().contains(ServletRequest.class)
  }

  def "Sets alternative AlternateTypeProvider with a rule"() {
    given:
      def provider = new AlternateTypeProvider()
      def rule = newMapRule(String, String)

    when:
      plugin.alternateTypeProvider(provider)
            .alternateTypeRules(rule)
            .build()
    then:
      plugin.swaggerGlobalSettings.alternateTypeProvider == provider
      plugin.swaggerGlobalSettings.alternateTypeProvider.rules.contains(rule)

  }

  def "should contain both default and custom exclude annotations"() {
    when:
      plugin.excludeAnnotations(AbstractSingletonProxyFactoryBean.class, ProxyFactoryBean.class).build()

    then:
      plugin.apiListingReferenceScanner.excludeAnnotations.containsAll([
            AbstractSingletonProxyFactoryBean.class,
            ProxyFactoryBean.class,
            ApiIgnore.class
      ])
  }

  @Unroll
  def "Basic property checks"() {
    when:
      plugin."$builderMethod"(object)

    then:
      plugin."$property" == object

    where:
      builderMethod          | object                                                         | property
      'modelProvider'        | new DefaultModelProvider(null, null, null)                     | 'modelProvider'
      'pathProvider'         | new AbsoluteSwaggerPathProvider()                              | 'swaggerPathProvider'
      'authorizationTypes'   | new ArrayList<AuthorizationType>()                             | 'authorizationTypes'
      'authorizationContext' | new AuthorizationContext.AuthorizationContextBuilder().build() | 'authorizationContext'
      'includePatterns'      | ['one', 'two', 'three'] as String[]                            | 'includePatterns'
      'swaggerGroup'         | 'someGroup'                                                    | 'swaggerGroup'
  }

  def "non nullable swaggerApiResourceListing properties"() {

    when:
      plugin.build()

    then:
      SwaggerApiResourceListing listing = plugin.swaggerApiResourceListing
      null != listing.swaggerGlobalSettings
      null != listing.swaggerPathProvider
      null != listing.apiInfo
      null != listing.apiListingReferenceScanner

      listing.swaggerGlobalSettings == plugin.swaggerGlobalSettings
      listing.swaggerPathProvider == plugin.swaggerPathProvider
      listing.apiInfo == plugin.apiInfo
      listing.apiListingReferenceScanner instanceof ApiListingReferenceScanner
      listing.swaggerCache instanceof SwaggerCache
      listing.swaggerGroup == plugin.swaggerGroup

  }

  def "ApiListingReferenceScanner properties"() {
    when:
      plugin.build()
      ApiListingReferenceScanner apiListingReferenceScanner = plugin.apiListingReferenceScanner

    then:
      apiListingReferenceScanner.excludeAnnotations == plugin.excludeAnnotations + springSwaggerConfig.defaultExcludeAnnotations()
      apiListingReferenceScanner.resourceGroupingStrategy == plugin.resourceGroupingStrategy
      apiListingReferenceScanner.swaggerPathProvider == plugin.swaggerPathProvider
      apiListingReferenceScanner.swaggerGroup == plugin.swaggerGroup
      apiListingReferenceScanner.includePatterns == plugin.includePatterns

  }


}
