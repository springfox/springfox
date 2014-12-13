package com.mangofactory.swagger.generics;

import org.springframework.http.HttpStatus;

/**
 * Class for testing deep generics
 */
public class ResponseEntityAlternative<T> extends HttpEntityAlternative<T> {
  private HttpStatus statusCode;

  public HttpStatus getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(HttpStatus statusCode) {
    this.statusCode = statusCode;
  }
}
