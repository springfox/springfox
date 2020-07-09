package springfox.test.contract.oas.bugs;

import io.swagger.annotations.ApiModelProperty;

public class Bug2767 {
  public static class ErrorResponse {
    @ApiModelProperty(required = true, example = "address")
    private String invalidField;

    public String getInvalidField() {
      return invalidField;
    }

    public void setInvalidField(String invalidField) {
      this.invalidField = invalidField;
    }
  }

  public static class Response {
    @ApiModelProperty(required = true, example = "some data")
    private String data;

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }
  }
}
