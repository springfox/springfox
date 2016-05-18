package springfox.bean.validators.plugins.models;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author : ashutosh
 *         18/05/2016
 */
public class PatternAndSizeTestModel {

  @Size(min = 3, max = 5)
  @Pattern(regexp = "[a-zA-Z0-9_]")
  private String propertyString;

  private String getterString;

  public String getPropertyString() {
    return propertyString;
  }

  @Size(min = 1, max = 4)
  @Pattern(regexp = "[A-Z]")
  public String getGetterString() {
    return getterString;
  }
}
