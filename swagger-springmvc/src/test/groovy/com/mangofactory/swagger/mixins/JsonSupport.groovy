package com.mangofactory.swagger.mixins

import groovy.json.JsonOutput

//import com.wordnik.swagger.core.util.JsonSerializer
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.test.web.servlet.MvcResult
@Slf4j
class JsonSupport {

   def jsonBodyResponse(MvcResult jsonResponse, print = false) {
      String content = jsonResponse.response.getContentAsString()
      if (!content) {
         return content
      }
      if(print){
        log.info(JsonOutput.prettyPrint(content))
      }
      def slurper = new JsonSlurper()
      return slurper.parseText(content)
   }

  def String swaggerCoreSerialize(obj){
//    JsonSerializer.asJson(obj);
  }
}
