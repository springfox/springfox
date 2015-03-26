package springfox.documentation.service;

import java.util.List;

public class AllowableListValues implements AllowableValues {
  private final List<String> values;
  private final String valueType;

  public AllowableListValues(List<String> values, String valueType) {
    this.values = values;
    this.valueType = valueType;
  }

  public List<String> getValues() {
    return values;
  }

  public String getValueType() {
    return valueType;
  }
}
