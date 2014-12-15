package com.mangofactory.swagger.models.dto;

/**
 * THe swagger 1.2 spec does not support $refs on operations hence this class
 * https://github.com/swagger-api/swagger-spec/blob/master/versions/1.2.md#523-operation-object:
 * "The type field MUST be used to link to other models."
 */
public class TypeOnlyDataType implements SwaggerDataType {

  private final String type;

  public TypeOnlyDataType(SwaggerDataType swaggerDataType) {
    this.type = swaggerDataType.getAbsoluteType();
  }

  public String getType() {
    return type;
  }

  @Override
  public String getAbsoluteType() {
    return type;
  }
}
