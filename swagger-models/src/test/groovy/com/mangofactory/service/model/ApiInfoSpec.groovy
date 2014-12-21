package com.mangofactory.service.model

import com.mangofactory.service.model.builder.ApiInfoBuilder

class ApiInfoSpec extends InternalJsonSerializationSpec {

  final ApiInfo apiInfo = new ApiInfoBuilder()
          .description('Api Description')
          .contact('Contact Email')
          .license('Licence Type')
          .licenseUrl('License URL')
          .termsOfServiceUrl('Api terms of service')
          .title('Title')
          .build()

  def "should serialize"() {
    expect:
      writePretty(apiInfo) == """{
  "contact" : "Contact Email",
  "description" : "Api Description",
  "license" : "Licence Type",
  "licenseUrl" : "License URL",
  "termsOfServiceUrl" : "Api terms of service",
  "title" : "Title"
}"""
  }

  def "should pass coverage"() {
    expect:
      apiInfo.with {
        getContact()
        getDescription()
        getLicense()
        getLicenseUrl()
        getTitle()
        getTermsOfServiceUrl()
      }
  }
}
