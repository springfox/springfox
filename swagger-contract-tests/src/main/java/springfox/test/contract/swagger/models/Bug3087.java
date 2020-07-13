package springfox.test.contract.swagger.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Bug3087 {
  @ApiModelProperty(required = true, position = 2)
  private String user;
  
  @ApiModelProperty(required = true, position = 1)
  private String password;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}