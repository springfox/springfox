package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseMessage {
  private final int code;
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private final String message;
  private final String responseModel;

  public ResponseMessage(int code, String message, String responseModel) {
    this.code = code;
    this.message = message;
    this.responseModel = responseModel;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getResponseModel() {
    return responseModel;
  }
}
