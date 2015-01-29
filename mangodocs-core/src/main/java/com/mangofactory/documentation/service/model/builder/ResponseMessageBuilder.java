package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.ResponseMessage;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class ResponseMessageBuilder {
  private int code;
  private String message;
  private String responseModel;

  public ResponseMessageBuilder code(int code) {
    this.code = code;
    return this;
  }

  public ResponseMessageBuilder message(String message) {
    this.message = defaultIfAbsent(message, this.message);
    return this;
  }

  public ResponseMessageBuilder responseModel(String responseModel) {
    this.responseModel = defaultIfAbsent(responseModel, this.responseModel);
    return this;
  }

  public ResponseMessage build() {
    return new ResponseMessage(code, message, responseModel);
  }
}