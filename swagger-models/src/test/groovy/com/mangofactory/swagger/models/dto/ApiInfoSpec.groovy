package com.mangofactory.swagger.models.dto

class ApiInfoSpec extends InternalJsonSerializationSpec {

  final ApiInfo apiInfo = new ApiInfo(
          " Title",
          "Api Description",
          "Api terms of service",
          "Contact Email",
          "Licence Type",
          "License URL"
  )

  def "should serialize"() {
    expect:
      writePretty(apiInfo) == """{
  "contact" : "Contact Email",
  "description" : "Api Description",
  "license" : "Licence Type",
  "licenseUrl" : "License URL",
  "termsOfServiceUrl" : "Api terms of service",
  "title" : " Title"
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
