package com.mangofactory.documentation.swagger.mappers;

import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.swagger.dto.DataType;
import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper
public class DataTypeMapper {

  @ResponseTypeName
  public String responseTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    return modelRef.getType();
  }

  @OperationType
  public com.mangofactory.documentation.swagger.dto.DataType operationTypeFromModelRef(ModelRef modelRef) {
    return new DataType(operationTypeName(modelRef));
  }

  @Type
  public com.mangofactory.documentation.swagger.dto.DataType typeFromModelRef(ModelRef modelRef) {
    return new DataType(modelRef.getType());
  }

  @ItemType
  public com.mangofactory.documentation.swagger.dto.DataType itemTypeFromModelRef(ModelRef modelRef) {
    if (modelRef.isCollection()) {
      return new DataType(modelRef.getItemType());
    }
    return null;
  }

  private String operationTypeName(ModelRef modelRef) {
    if (modelRef == null) {
      return null;
    }
    if (modelRef.isCollection()) {
      return String.format("%s[%s]", modelRef.getType(), modelRef.getItemType());
    }
    return modelRef.getType();
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface OperationType {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Type {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ResponseTypeName {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface ItemType {
  }
}
