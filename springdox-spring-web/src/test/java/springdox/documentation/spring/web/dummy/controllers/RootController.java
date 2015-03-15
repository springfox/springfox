package springdox.documentation.spring.web.dummy.controllers;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@Api(value = "/", position = 0)
public class RootController {
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public
  @ResponseBody
  List<String> getAll() {
    return Arrays.asList(new String[]{"1", "2"});
  }
}