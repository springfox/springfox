package springdox.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseMessage {
  private int code;
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private String message;
  private String responseModel;

  public ResponseMessage() {
  }

  public ResponseMessage(int code, String message, String responseModel) {
    this.code = code;
    this.message = message;
    this.responseModel = responseModel;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getResponseModel() {
    return responseModel;
  }

  public void setResponseModel(String responseModel) {
    this.responseModel = responseModel;
  }
}
