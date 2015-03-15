package springdox.documentation.spring.web.dummy.controllers;

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractController<T> {

  @RequestMapping(value = "/create-t", method = RequestMethod.PUT)
  public void create(T toCreate) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/get-t", method = RequestMethod.GET)
  @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
  public T get() {
    throw new UnsupportedOperationException();
  }
}
