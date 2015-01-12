package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.service.model.ModelRef;
import com.mangofactory.swagger.dto.DataType;
import org.springframework.stereotype.Component;

@Component
public class DataTypeMapper {
  public com.mangofactory.swagger.dto.SwaggerDataType map(com.fasterxml.classmate.ResolvedType value) {
    return new DataType(ResolvedTypes.typeName(value));
  }
  public com.mangofactory.swagger.dto.DataType fromModelRef(ModelRef modelRef) {
    if (modelRef != null) {
      return new DataType(modelRef.getType());
    }
    return null;
  }
}
