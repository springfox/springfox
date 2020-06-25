/*
 *
 *  Copyright 2015-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.integration

import com.fasterxml.classmate.TypeResolver
import org.joda.time.LocalDate
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.http.ResponseEntity
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ResponseMessage
import springfox.documentation.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration

import javax.servlet.ServletRequest

import static java.util.Collections.*
import static org.springframework.http.HttpStatus.*
import static org.springframework.web.bind.annotation.RequestMethod.*
import static springfox.documentation.schema.AlternateTypeRules.*

class DocketSpec extends DocumentationContextSpec {

  def "Should have sensible defaults when built with minimal configuration"() {
    when:
    plugin.configure(contextBuilder)
    def pluginContext = contextBuilder.build()

    then:
    pluginContext.groupName == 'default'
    pluginContext.securitySchemes.isEmpty()
    pluginContext.apiInfo.getTitle() == "Api Documentation"
    pluginContext.apiInfo.getDescription() == "Api Documentation"
    pluginContext.apiInfo.getTermsOfServiceUrl() == 'urn:tos'

    def contact = pluginContext.apiInfo.getContact()
    contact.name == ''
    contact.email == ''
    contact.url == ''
    pluginContext.apiInfo.getLicense() == 'Apache 2.0'
    pluginContext.apiInfo.getLicenseUrl() == "http://www.apache.org/licenses/LICENSE-2.0"
    pluginContext.apiInfo.version == "1.0"

    pluginContext.pathProvider instanceof DummyPathProvider // this one is dummy as this is what we have in test ctx
  }

  def "Swagger global response messages should override the default for a particular RequestMethod"() {
    when:
    plugin
        .globalResponseMessage(GET, [new ResponseMessage(
        OK.value(),
        "blah",
        null,
        [],
        [] as Map,
        [])])
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

  def "Swagger global response messages should not be used for a particular RequestMethod"() {
    when:
    new Docket(DocumentationType.SWAGGER_12)
        .globalResponseMessage(GET, [new ResponseMessage(
        OK.value(),
        "blah",
        null,
        [],
        [] as Map,
        [])])
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
    new Docket(DocumentationType.SWAGGER_12)
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
    new Docket(DocumentationType.SWAGGER_12)
        .alternateTypeRules(rule)
        .configure(contextBuilder)

    expect:
    documentationContext().alternateTypeProvider.rules.contains(rule)
  }

  def "Model substitution registers new rules"() {
    when:
    def swaggerDefault = new SwaggerDefaultConfiguration(
        new Defaults(),
        new TypeResolver(),
        new DefaultPathProvider())
        .create(DocumentationType.SWAGGER_12)
    def javaVersion = System.getProperty("java.version")
    def isjdk8 = javaVersion.startsWith("1.8") || javaVersion.startsWith("11")
    def jdk8RuleCount = (isjdk8 ? 6 : 0)

    and:
    new Docket(DocumentationType.SWAGGER_12)
        ."${method}"(*args)
        .configure(swaggerDefault)

    then:
    swaggerDefault.build().alternateTypeProvider.rules.size() == expectedSize + jdk8RuleCount

    where:
    method                    | args                               | expectedSize
    'genericModelSubstitutes' | [ResponseEntity.class, List.class] | 17
    'directModelSubstitute'   | [LocalDate.class, Date.class]      | 16
  }

  def "Basic property checks"() {
    when:
    plugin."$builderMethod"(object)

    then:
    documentationContext()."$property" == object

    where:
    builderMethod      | object                                  | property
    'pathProvider'     | new DefaultPathProvider()               | 'pathProvider'
    'securitySchemes'  | new ArrayList<SecurityScheme>()         | 'securitySchemes'
    'securityContexts' | validContexts()                         | 'securityContexts'
    'groupName'        | 'someGroup'                             | 'groupName'
    'apiInfo'          | new ApiInfo('', '', "", '', '', '', '') | 'apiInfo'
    'apiInfo'          | ApiInfo.DEFAULT                         | 'apiInfo'
  }

  def validContexts() {
    singletonList(SecurityContext.builder().build())
  }

  def "non nullable swaggerApiResourceListing properties"() {

    when:
    new Docket(DocumentationType.SWAGGER_12)
        .configure(contextBuilder)

    and:
    def pluginContext = contextBuilder.build()

    then:
    "default" == pluginContext.groupName
    null != pluginContext.pathProvider
    null != pluginContext.apiInfo
    null != pluginContext.apiSelector
    null != pluginContext.globalResponseMessages
    null != pluginContext.ignorableParameterTypes
    null != pluginContext.listingReferenceOrdering
    null != pluginContext.apiDescriptionOrdering

  }
}
