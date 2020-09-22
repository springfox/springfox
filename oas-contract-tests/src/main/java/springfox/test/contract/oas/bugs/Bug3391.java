package springfox.test.contract.oas.bugs;

import io.swagger.annotations.ApiModelProperty;

public class Bug3391 {
    @ApiModelProperty(required = true, position = 2)
    private String bar;

    @ApiModelProperty(required = true, position = 1)
    private String foo;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}
