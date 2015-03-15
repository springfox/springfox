package com.mangofactory.documentation.spring.web.mixins

import com.mangofactory.documentation.service.ApiDescription


class ApiDescriptionSupport {

  def apiDescriptions(List<String> paths) {
    def result = []
    paths.each {
      result << new ApiDescription(it, "", [], false)
    }
    result
  }

}
