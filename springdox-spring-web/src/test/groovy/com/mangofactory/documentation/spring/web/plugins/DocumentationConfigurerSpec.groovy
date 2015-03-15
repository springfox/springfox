package com.mangofactory.documentation.spring.web.plugins
import com.google.common.collect.Ordering
import com.mangofactory.documentation.RequestMappingPatternMatcher
import com.mangofactory.documentation.annotations.ApiIgnore
import com.mangofactory.documentation.service.ApiDescription
import com.mangofactory.documentation.service.ApiInfo
import com.mangofactory.documentation.service.AuthorizationType
import com.mangofactory.documentation.service.ResponseMessage
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.contexts.AuthorizationContext
import com.mangofactory.documentation.spi.service.contexts.Defaults
import com.mangofactory.documentation.spring.web.RelativePathProvider
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.joda.time.LocalDate
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.http.ResponseEntity

import javax.servlet.ServletContext
import javax.servlet.ServletRequest

import static com.mangofactory.documentation.schema.AlternateTypeRules.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.web.bind.annotation.RequestMethod.*

class DocumentationConfigurerSpec extends DocumentationContextSpec {

  def "Should have sensible defaults when built with minimal configuration"() {
    when:
      def pluginContext = plugin.configure(contextBuilder)
    then:
      pluginContext.groupName == 'default'
      pluginContext.authorizationTypes == null
      pluginContext.apiInfo.getTitle() == "Api Documentation"
      pluginContext.apiInfo.getDescription() == "Api Documentation"
      pluginContext.apiInfo.getTermsOfServiceUrl() == 'urn:tos'
      pluginContext.apiInfo.getContact() == 'Contact Email'
      pluginContext.apiInfo.getLicense() == 'Apache 2.0'
      pluginContext.apiInfo.getLicenseUrl() ==  "http://www.apache.org/licenses/LICENSE-2.0"
      pluginContext.apiInfo.version == "1.0"

      pluginContext.pathProvider instanceof RelativePathProvider
  }

  def "Swagger global response messages should override the default for a particular RequestMethod"() {
    when:
      plugin
              .globalResponseMessage(GET, [new ResponseMessage(OK.value(), "blah", null)])
              .useDefaultResponseMessages(true)
              .configure(contextBuilder)

    and:
      def pluginContext = contextBuilder.build()
    then:
      pluginContext.getGlobalResponseMessages()[GET][0].getMessage() == "blah"
      pluginContext.getGlobalResponseMessages()[GET].size() == 1

    and: "defaults are preserved"
      pluginContext.getGlobalResponseMessages().keySet().containsAll(
              [POST, PUT, DELETE, PATCH, TRACE, OPTIONS, HEAD]
      )
  }

  def "Verify configurer behavior"() {
    when:
      plugin.enable(true)
    then:
      plugin.isEnabled()
      !plugin.supports(DocumentationType.SPRING_WEB)
      plugin.supports(DocumentationType.SWAGGER_12)
      plugin.documentationType == DocumentationType.SWAGGER_12
  }

  def "Swagger global response messages should not be used for a particular RequestMethod"() {
    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .globalResponseMessage(GET, [new ResponseMessage(OK.value(), "blah", null)])
              .useDefaultResponseMessages(false)
              .configure(contextBuilder)

    and:
      def pluginContext = contextBuilder.build()
    then:
      pluginContext.getGlobalResponseMessages()[GET][0].getMessage() == "blah"
      pluginContext.getGlobalResponseMessages()[GET].size() == 1

    and: "defaults are preserved"
      pluginContext.getGlobalResponseMessages().keySet().containsAll([GET])
  }

  def "Swagger ignorableParameterTypes should append to the default ignorableParameterTypes"() {
    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .ignoredParameterTypes(AbstractSingletonProxyFactoryBean.class, ProxyFactoryBean.class)
              .configure(contextBuilder)
    and:
      def pluginContext = contextBuilder.build()
    then:
      pluginContext.getIgnorableParameterTypes().contains(AbstractSingletonProxyFactoryBean.class)
      pluginContext.getIgnorableParameterTypes().contains(ProxyFactoryBean.class)

    and: "one of the defaults"
      pluginContext.getIgnorableParameterTypes().contains(ServletRequest.class)
  }

  def "Sets alternative AlternateTypeProvider with a rule"() {
    given:
      def rule = newMapRule(String, String)
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .alternateTypeRules(rule)
              .configure(contextBuilder)
    expect:
      context().alternateTypeProvider.rules.contains(rule)
  }

  def "Model substitution registers new rules"() {
    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              ."${method}"(*args)
              .configure(contextBuilder)
    then:
      context().alternateTypeProvider.rules.size() == expectedSize

    where:
      method                    | args                               | expectedSize
      'genericModelSubstitutes' | [ResponseEntity.class, List.class] | 8
      'directModelSubstitute'   | [LocalDate.class, Date.class]      | 7
  }


  def "should contain both default and custom exclude annotations"() {
    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .excludeAnnotations(ApiOperation.class, Api.class)
              .configure(contextBuilder)

    and:
      def pluginContext = contextBuilder.build()
    then:
      pluginContext.requestMappingEvaluator.excludeAnnotations.containsAll([
              ApiOperation.class,
              Api.class
      ])
  }

  def "should preserve default exclude annotations"() {
    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .excludeAnnotations(Api.class, ApiOperation.class)
              .configure(contextBuilder)

    and:
      def pluginContext = contextBuilder.build()
    then:
      pluginContext.requestMappingEvaluator.excludeAnnotations.containsAll([
              Api.class,
              ApiOperation.class,
              ApiIgnore.class
      ])

  }

  def "Basic property checks"() {
    when:
      plugin."$builderMethod"(object)

    then:
      context()."$property" == object

    where:
      builderMethod          | object                                         | property
      'pathProvider'         | new RelativePathProvider(Mock(ServletContext)) | 'pathProvider'
      'authorizationTypes'   | new ArrayList<AuthorizationType>()             | 'authorizationTypes'
      'authorizationContext' | validContext()                                 | 'authorizationContext'
      'groupName'            | 'someGroup'                                    | 'groupName'
      'apiInfo'              | new ApiInfo('', '', "", '', '', '', '')        | 'apiInfo'
      'apiDescriptionOrdering'| apiDescriptionOrdering()                      | 'apiDescriptionOrdering'
      'operationOrdering'     | operationOrdering()                           | 'operationOrdering'
      'produces'              | ['application/json'] as Set                   | 'produces'
      'consumes'              | ['application/json'] as Set                   | 'consumes'
      'protocols'             | ['application/json'] as Set                   | 'protocols'
  }

  Ordering<ApiDescription> apiDescriptionOrdering() {
    new Defaults().apiDescriptionOrdering()
  }

  Ordering<ApiDescription> operationOrdering() {
    new Defaults().operationOrdering()
  }

  private AuthorizationContext validContext() {
    new AuthorizationContext.AuthorizationContextBuilder()
            .withRequestMappingPatternMatcher(Mock(RequestMappingPatternMatcher))
            .build()
  }

  def "non nullable swaggerApiResourceListing properties"() {

    when:
      new DocumentationConfigurer(DocumentationType.SWAGGER_12)
              .configure(contextBuilder)

    and:
      def pluginContext = contextBuilder.build()
    then:
      "default" == pluginContext.groupName
      null != pluginContext.pathProvider
      null != pluginContext.apiInfo
      null != pluginContext.requestMappingEvaluator
      null != pluginContext.globalResponseMessages
      null != pluginContext.ignorableParameterTypes
      null != pluginContext.listingReferenceOrdering
      null != pluginContext.apiDescriptionOrdering
      null != pluginContext.produces
      null != pluginContext.protocols
      null != pluginContext.consumes

  }


}
