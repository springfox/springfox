package com.mangofactory.service.model;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResponseMessage that = (ResponseMessage) o;

    return code == that.code;

  }

  @Override
  public int hashCode() {
    return code;
  }
}
