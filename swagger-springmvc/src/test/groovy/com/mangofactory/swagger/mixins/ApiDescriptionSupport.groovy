package com.mangofactory.swagger.mixins

import com.wordnik.swagger.model.ApiDescription

import static com.mangofactory.swagger.ScalaUtils.emptyScalaList
import static com.mangofactory.swagger.ScalaUtils.toOption

class ApiDescriptionSupport {

  def apiDescriptions(List<String> paths) {
    def result = []
    paths.each {
      result << new ApiDescription(it, toOption(""), emptyScalaList())
    }
    result
  }

}
