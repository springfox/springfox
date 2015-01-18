package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.schema.TypeNameExtractor;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ModelRef;
import com.mangofactory.swagger.dto.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mangofactory.schema.plugins.ModelContext.*;

@Component
public class DataTypeMapper {
  private final TypeNameExtractor extractor;

  @Autowired
  public DataTypeMapper(TypeNameExtractor extractor) {
    this.extractor = extractor;
  }

  public com.mangofactory.swagger.dto.SwaggerDataType fromResolvedType(com.fasterxml.classmate.ResolvedType value) {
    return new DataType(extractor.typeName(inputParamWithoutContainerType(value, DocumentationType.SWAGGER_12)));
  }

  public com.mangofactory.swagger.dto.DataType fromModelRef(ModelRef modelRef) {
    if (modelRef != null) {
      return new DataType(modelRef.getType());
    }
    return null;
  }
}
