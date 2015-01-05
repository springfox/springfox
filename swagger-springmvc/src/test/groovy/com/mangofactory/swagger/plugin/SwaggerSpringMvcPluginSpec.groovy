package com.mangofactory.swagger.plugin

import com.mangofactory.service.model.ApiInfo
import com.mangofactory.service.model.AuthorizationType
import com.mangofactory.service.model.ResponseMessage
import com.mangofactory.springmvc.plugins.DocumentationContext
import com.mangofactory.springmvc.plugins.DocumentationContextBuilder
import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.core.ClassOrApiAnnotationResourceGrouping
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering
import com.mangofactory.swagger.paths.AbsoluteSwaggerPathProvider
import com.mangofactory.swagger.paths.RelativeSwaggerPathProvider
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.joda.time.LocalDate
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.ServletContext
import javax.servlet.ServletRequest

import static com.mangofactory.schema.alternates.Alternates.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.web.bind.annotation.RequestMethod.*

@Mixin([SpringSwaggerConfigSupport, DocumentationContextSupport])
class SwaggerSpringMvcPluginSpec extends Specification {
  Defaults defaultValues = defaults(Mock(ServletContext))
  DocumentationContextBuilder contextBuilder = defaultContextBuilder(defaultValues)

  DocumentationContext pluginContext

  def "Should have sensible defaults when built with minimal configuration"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin().build(contextBuilder)

    then:
      pluginContext.groupName == 'default'
      pluginContext.authorizationTypes == null

      pluginContext.apiInfo.getTitle() == 'default Title'
      pluginContext.apiInfo.getDescription() == 'Api Description'
      pluginContext.apiInfo.getTermsOfServiceUrl() == 'Api terms of service'
      pluginContext.apiInfo.getContact() == 'Contact Email'
      pluginContext.apiInfo.getLicense() == 'Licence Type'
      pluginContext.apiInfo.getLicenseUrl() == 'License URL'
      pluginContext.apiInfo.version == "1.0"

      pluginContext.resourceGroupingStrategy instanceof ClassOrApiAnnotationResourceGrouping
      pluginContext.swaggerPathProvider instanceof RelativeSwaggerPathProvider
  }

  def "Swagger global response messages should override the default for a particular RequestMethod"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .globalResponseMessage(GET, [new ResponseMessage(OK.value(), "blah", null)])
              .useDefaultResponseMessages(true)
              .build(contextBuilder)

    then:
      pluginContext.getGlobalResponseMessages()[GET][0].getMessage() == "blah"
      pluginContext.getGlobalResponseMessages()[GET].size() == 1

    and: "defaults are preserved"
      pluginContext.getGlobalResponseMessages().keySet().containsAll(
              [POST, PUT, DELETE, PATCH, TRACE, OPTIONS, HEAD]
      )
  }

  def "Swagger global response messages should not be used for a particular RequestMethod"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .globalResponseMessage(GET, [new ResponseMessage(OK.value(), "blah", null)])
              .useDefaultResponseMessages(false)
              .build(contextBuilder)

    then:
      pluginContext.getGlobalResponseMessages()[GET][0].getMessage() == "blah"
      pluginContext.getGlobalResponseMessages()[GET].size() == 1

    and: "defaults are preserved"
      pluginContext.getGlobalResponseMessages().keySet().containsAll([GET])
  }

  def "Swagger ignorableParameterTypes should append to the default ignorableParameterTypes"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .ignoredParameterTypes(AbstractSingletonProxyFactoryBean.class, ProxyFactoryBean.class)
              .build(contextBuilder)
    then:
      pluginContext.getIgnorableParameterTypes().contains(AbstractSingletonProxyFactoryBean.class)
      pluginContext.getIgnorableParameterTypes().contains(ProxyFactoryBean.class)

    and: "one of the defaults"
      pluginContext.getIgnorableParameterTypes().contains(ServletRequest.class)
  }

  def "Sets alternative AlternateTypeProvider with a rule"() {
    given:
      def rule = newMapRule(String, String)
      pluginContext = new SwaggerSpringMvcPlugin()
              .alternateTypeRules(rule)
              .build(contextBuilder)
    expect:
      defaultValues.alternateTypeProvider.rules.contains(rule)
  }

  @Unroll
  def "Model substitution registers new rules"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              ."${method}"(*args)
              .build(contextBuilder)
    then:
      defaultValues.alternateTypeProvider.rules.size() == expectedSize

    where:
      method                    | args                               | expectedSize
      'genericModelSubstitutes' | [ResponseEntity.class, List.class] | 9
      'directModelSubstitute'   | [LocalDate.class, Date.class]      | 8
  }


  def "should contain both default and custom exclude annotations"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .excludeAnnotations(ApiOperation.class, Api.class)
              .build(contextBuilder)

    then:
      pluginContext.requestMappingEvaluator.excludeAnnotations.containsAll([
              ApiOperation.class,
              Api.class,
              ApiIgnore.class
      ])
  }

  @Unroll
  def "Basic property checks"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              ."$builderMethod"(object)
              .build(contextBuilder)

    then:
      pluginContext."$property" == object

    where:
      builderMethod          | object                                                         | property
      'pathProvider'         | new AbsoluteSwaggerPathProvider()                              | 'swaggerPathProvider'
      'authorizationTypes'   | new ArrayList<AuthorizationType>()                             | 'authorizationTypes'
      'authorizationContext' | new AuthorizationContext.AuthorizationContextBuilder().build() | 'authorizationContext'
//      'includePatterns'      | ['one', 'two', 'three'] as String[]                            | 'requestMappingEvaluator.includePatterns'
      'swaggerGroup'         | 'someGroup'                                                    | 'groupName'
      'apiInfo'              | new ApiInfo('', '', "", '', '', '', '')                        | 'apiInfo'
  }

  def "non nullable swaggerApiResourceListing properties"() {

    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .build(contextBuilder)

    then:
      "default" == pluginContext.groupName
      null != pluginContext.swaggerPathProvider
      null != pluginContext.resourceGroupingStrategy
      null != pluginContext.apiInfo
      null != pluginContext.requestMappingEvaluator
      null != pluginContext.globalResponseMessages
      null != pluginContext.ignorableParameterTypes
      pluginContext.listingReferenceOrdering instanceof ResourceListingLexicographicalOrdering
      pluginContext.apiDescriptionOrdering instanceof ApiDescriptionLexicographicalOrdering

  }

  def "should preserve default exclude annotations"() {
    when:
      pluginContext = new SwaggerSpringMvcPlugin()
              .excludeAnnotations(Api.class, ApiOperation.class)
              .build(contextBuilder)

    then:
      pluginContext.requestMappingEvaluator.excludeAnnotations.containsAll([
              Api.class,
              ApiOperation.class,
              ApiIgnore.class
      ])

    and: "the default excludes are unmodified"
      defaultValues.defaultExcludeAnnotations().size() == 1
  }
}
