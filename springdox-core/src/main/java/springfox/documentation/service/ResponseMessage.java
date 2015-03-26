package springfox.documentation.service;

import springfox.documentation.schema.ModelRef;

public class ResponseMessage {
  private final int code;
  private final String message;
  private final ModelRef responseModel;

  public ResponseMessage(int code, String message, ModelRef responseModel) {
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

  public ModelRef getResponseModel() {
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
