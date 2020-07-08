package springfox.test.contract.oas.bugs;

import io.swagger.annotations.ApiModelProperty;

public class Bug2982 {
  public static class MyClass {

    private MySubSclass subsclass;
    private String test;

    @ApiModelProperty(hidden = true)
    public MySubSclass getMySubSclass() {
      return subsclass;
    }

    public void SetMySubSclass(MySubSclass sc) {
      subsclass = sc;
    }

    public String getTest() {
      return test;
    }

    public void setTest(String test) {
      this.test = test;
    }
  }

  public static class MySubSclass {
    private String str;

    public String getStr() {
      return str;
    }

    public void setStr(String str) {
      this.str = str;
    }
  }
}
