package springdox.documentation.spring.web.mixins

import springdox.documentation.service.ApiDescription


class ApiDescriptionSupport {

  def apiDescriptions(List<String> paths) {
    def result = []
    paths.each {
      result << new ApiDescription(it, "", [], false)
    }
    result
  }

}
