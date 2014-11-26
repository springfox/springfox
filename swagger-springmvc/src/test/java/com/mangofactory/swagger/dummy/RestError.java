package com.mangofactory.swagger.dummy;

public class RestError {
  private String message;

  public RestError() {
    this("");
  }

  public RestError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
