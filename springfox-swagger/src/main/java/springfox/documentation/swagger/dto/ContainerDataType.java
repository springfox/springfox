package springfox.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Preconditions;

@JsonPropertyOrder({"type", "items"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerDataType implements SwaggerDataType {
  private String type = "array";
  private final Boolean uniqueItems;
  @JsonProperty
  private final SwaggerDataType items;

  public ContainerDataType(String innerType, boolean uniqueItems) {
    Preconditions.checkNotNull(innerType);
    Preconditions.checkArgument(!innerType.equals("array"), "Nested arrays not supported");
    items = new DataType(innerType);
    this.uniqueItems = uniqueItems ? true : null;
  }

  public String getType() {
    return type;
  }

  public Boolean isUniqueItems() {
    return uniqueItems;
  }

  public SwaggerDataType getItems() {
    return items;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
