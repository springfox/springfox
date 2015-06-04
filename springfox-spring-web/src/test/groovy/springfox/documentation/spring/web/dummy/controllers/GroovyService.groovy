package springfox.documentation.spring.web.dummy.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import springfox.documentation.spring.web.dummy.models.GroovyModel

@Controller
@RequestMapping("/groovy")
class GroovyService {
  @RequestMapping(value = "groovyModel", method = RequestMethod.GET)
  @ResponseBody
  public GroovyModel groovyModel() {
    return new GroovyModel("test");
  }

  @RequestMapping(value = "groovyModel", method = RequestMethod.PUT)
  public void updateGroovyModel(@RequestBody GroovyModel model) {
  }
}
