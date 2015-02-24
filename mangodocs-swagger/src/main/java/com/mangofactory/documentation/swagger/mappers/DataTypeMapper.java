package com.mangofactory.documentation.swagger.mappers;

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
  
  public String responseTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    //TODO: Verify this is not needed
//    if (modelRef.isCollection()) {
//      return String.format("%s[%s]", modelRef.getType(), modelRef.getItemType());
//    }
    return modelRef.getType();
  }

  public String operationTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return String.format("%s[%s]", modelRef.getType(), modelRef.getItemType());
    }
    return modelRef.getType();
  }
  
  public com.mangofactory.documentation.swagger.dto.DataType operationTypeFromModelRef(ModelRef modelRef) {
    return new DataType(operationTypeName(modelRef));
  }
  
  public com.mangofactory.documentation.swagger.dto.DataType typeFromModelRef(ModelRef modelRef) {
    return new DataType(modelRef.getType());
  }
  
  public com.mangofactory.documentation.swagger.dto.DataType itemTypeFromModelRef(ModelRef modelRef) {
    if (modelRef.isCollection()) {
      return new DataType(modelRef.getItemType());
    }
    return null;
  }
}
