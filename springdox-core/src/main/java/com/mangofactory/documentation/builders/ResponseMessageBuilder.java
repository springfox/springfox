package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.service.ResponseMessage;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ResponseMessageBuilder {
  private int code;
  private String message;
  private ModelRef responseModel;

  public ResponseMessageBuilder code(int code) {
    this.code = code;
    return this;
  }

  public ResponseMessageBuilder message(String message) {
    this.message = defaultIfAbsent(message, this.message);
    return this;
  }

  public ResponseMessageBuilder responseModel(ModelRef responseModel) {
    this.responseModel = defaultIfAbsent(responseModel, this.responseModel);
    return this;
  }

  public ResponseMessage build() {
    return new ResponseMessage(code, message, responseModel);
  }
}