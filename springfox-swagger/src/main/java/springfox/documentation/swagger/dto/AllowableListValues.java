package springfox.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AllowableListValues implements AllowableValues {
  @JsonProperty("enum")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> values;
  @JsonIgnore
  private String valueType;

  public AllowableListValues() {
  }

  public AllowableListValues(List<String> values, String valueType) {
    this.values = values;
    this.valueType = valueType;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }
}
