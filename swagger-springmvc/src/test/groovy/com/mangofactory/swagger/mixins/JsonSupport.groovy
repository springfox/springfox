package com.mangofactory.swagger.mixins

import groovy.json.JsonSlurper
import org.springframework.test.web.servlet.MvcResult

class JsonSupport {

   def jsonBodyResponse(MvcResult jsonResponse) {
      String content = jsonResponse.response.getContentAsString()
      if (!content) {
         return content
      }
      def slurper = new JsonSlurper()
      return slurper.parseText(content)
   }
}
