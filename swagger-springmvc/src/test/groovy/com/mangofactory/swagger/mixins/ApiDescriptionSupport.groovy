package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.models.dto.ApiDescription


class ApiDescriptionSupport {

  def apiDescriptions(List<String> paths) {
    def result = []
    paths.each {
      result << new ApiDescription(it, "", [], false)
    }
    result
  }

}
