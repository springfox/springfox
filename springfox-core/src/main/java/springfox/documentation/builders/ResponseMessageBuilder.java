package springfox.documentation.builders;

import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;

public class ResponseMessageBuilder {
  private int code;
  private String message;
  private ModelRef responseModel;

  public ResponseMessageBuilder code(int code) {
    this.code = code;
    return this;
  }

  public ResponseMessageBuilder message(String message) {
    this.message = BuilderDefaults.defaultIfAbsent(message, this.message);
    return this;
  }

  public ResponseMessageBuilder responseModel(ModelRef responseModel) {
    this.responseModel = BuilderDefaults.defaultIfAbsent(responseModel, this.responseModel);
    return this;
  }

  public ResponseMessage build() {
    return new ResponseMessage(code, message, responseModel);
  }
}