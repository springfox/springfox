package com.mangofactory.swagger.plugin
import com.mangofactory.service.model.ApiInfo
import com.mangofactory.service.model.AuthorizationType
import com.mangofactory.service.model.ResponseMessage
import com.mangofactory.spring.web.RelativePathProvider
import com.mangofactory.spring.web.annotations.ApiIgnore
import com.mangofactory.spring.web.ordering.ApiDescriptionLexicographicalOrdering
import com.mangofactory.spring.web.ordering.ResourceListingLexicographicalOrdering
import com.mangofactory.spring.web.plugins.AuthorizationContext
import com.mangofactory.spring.web.plugins.DocumentationConfigurer
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.web.AbsolutePathProvider
import com.mangofactory.swagger.web.ClassOrApiAnnotationResourceGrouping
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.joda.time.LocalDate
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.http.ResponseEntity

import javax.servlet.ServletRequest

import static com.mangofactory.schema.alternates.Alternates.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.web.bind.annotation.RequestMethod.*

@Mixin([SpringSwaggerConfigSupport, DocumentationContextSupport])
class SwaggerSpringMvcPluginSpec extends DocumentationContextSpec {

  def "Should have sensible defaults when built with minimal configuration"() {
    when:
      def pluginContext = new DocumentationConfigurer().build(contextBuilder)

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
      pluginContext.pathProvider instanceof RelativePathProvider
  }

  def "Swagger global response messages should override the default for a particular RequestMethod"() {
    when:
      def pluginContext = new DocumentationConfigurer()
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
      def pluginContext = new DocumentationConfigurer()
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
      def pluginContext = new DocumentationConfigurer()
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
      new DocumentationConfigurer()
              .alternateTypeRules(rule)
              .build(contextBuilder)
    expect:
      defaultValues.alternateTypeProvider.rules.contains(rule)
  }

  def "Model substitution registers new rules"() {
    when:
      new DocumentationConfigurer()
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
      def pluginContext = new DocumentationConfigurer()
              .excludeAnnotations(ApiOperation.class, Api.class)
              .build(contextBuilder)

    then:
      pluginContext.requestMappingEvaluator.excludeAnnotations.containsAll([
              ApiOperation.class,
              Api.class,
              ApiIgnore.class
      ])
  }

  def "Basic property checks"() {
    when:
      def pluginContext = new DocumentationConfigurer()
              ."$builderMethod"(object)
              .build(contextBuilder)

    then:
      pluginContext."$property" == object

    where:
      builderMethod          | object                                                         | property
      'pathProvider'         | new AbsolutePathProvider()                                     | 'pathProvider'
      'authorizationTypes'   | new ArrayList<AuthorizationType>()                             | 'authorizationTypes'
      'authorizationContext' | new AuthorizationContext.AuthorizationContextBuilder().build() | 'authorizationContext'
      'groupName'            | 'someGroup'                                                    | 'groupName'
      'apiInfo'              | new ApiInfo('', '', "", '', '', '', '')                        | 'apiInfo'
  }

  def "non nullable swaggerApiResourceListing properties"() {

    when:
      def pluginContext = new DocumentationConfigurer()
              .build(contextBuilder)

    then:
      "default" == pluginContext.groupName
      null != pluginContext.pathProvider
      null != pluginContext.apiInfo
      null != pluginContext.requestMappingEvaluator
      null != pluginContext.globalResponseMessages
      null != pluginContext.ignorableParameterTypes
      pluginContext.listingReferenceOrdering instanceof ResourceListingLexicographicalOrdering
      pluginContext.apiDescriptionOrdering instanceof ApiDescriptionLexicographicalOrdering

  }

  def "should preserve default exclude annotations"() {
    when:
      def pluginContext = new DocumentationConfigurer()
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
