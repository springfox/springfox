package com.mangofactory.documentation.swagger.dto.mappers;

import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.swagger.dto.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataTypeMapper {
  private final TypeNameExtractor extractor;

  @Autowired
  public DataTypeMapper(TypeNameExtractor extractor) {
    this.extractor = extractor;
  }

  public com.mangofactory.documentation.swagger.dto.SwaggerDataType fromTypeName(String typeName) {
    return new DataType(typeName);
  }

  public com.mangofactory.documentation.swagger.dto.DataType fromModelRef(ModelRef modelRef) {
    if (modelRef.isCollection()) {
      return new DataType(modelRef.getItemType());
    }
    return null;
  }
}
