package springfox.documentation.swagger.models;

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ServiceWithAnnotationOnInterface {

  public static class SimpleServiceImpl implements SimpleService {

    @Override
    public Object aMethod() {
      return null;
    }
  }

  public static interface SimpleService {

    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = 201, message = "201 Created")
    })
    Object aMethod();
  }

}
