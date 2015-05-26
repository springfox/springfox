package springfox.documentation.spring.web.dummy.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.spring.web.dummy.models.GroovyModel

@Controller
@RequestMapping("/groovy")
class GroovyService {
  @RequestMapping(value = "groovyModel", method = RequestMethod.GET)
  public GroovyModel groovyModel() {
    return new GroovyModel("test");
  }
}
