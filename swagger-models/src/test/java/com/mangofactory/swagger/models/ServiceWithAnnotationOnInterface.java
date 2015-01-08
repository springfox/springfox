package com.mangofactory.swagger.models;

import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

public class ServiceWithAnnotationOnInterface {

  public static class SimpleServiceImpl implements SimpleService {

    @Override
    public Object aMethod() {
      return null;
    }
  }

  public static interface SimpleService {

    @ApiResponses({
            @ApiResponse(code = 201, message = "201 Created")
    })
    Object aMethod();
  }

}
