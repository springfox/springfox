package springdox.documentation.swagger.dto;

public class PrimitiveDataType implements SwaggerDataType {
  private String type;

  public PrimitiveDataType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
