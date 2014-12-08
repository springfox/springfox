package com.mangofactory.swagger.models.dto;

public class ResponseMessage {
  private final int code;
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
