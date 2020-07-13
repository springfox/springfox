package springfox.test.contract.swagger.webflux.bugs;

public class Bug3343 {
  public static class Payload {
    private String content;

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }

  public static class SuccessResponse<T> {
    private int code;
    private T payload;

    public T getPayload() {
      return payload;
    }

    public void setPayload(T payload) {
      this.payload = payload;
    }

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }
  }
}
