package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.swagger.dto.ContainerDataType;
import com.mangofactory.swagger.dto.DataType;
import com.mangofactory.swagger.dto.ModelRef;
import com.mangofactory.swagger.dto.PrimitiveDataType;
import com.mangofactory.swagger.dto.PrimitiveFormatDataType;
import com.mangofactory.swagger.dto.ReferenceDataType;
import org.mapstruct.Mapper;

@Mapper
public abstract class DataTypeMapper {
  public abstract PrimitiveDataType toSwagger(com.mangofactory.service.model.PrimitiveDataType from);
  public abstract PrimitiveFormatDataType toSwagger(com.mangofactory.service.model.PrimitiveFormatDataType from);
  public abstract ReferenceDataType toSwagger(com.mangofactory.service.model.ReferenceDataType from);
  public abstract ModelRef toSwagger(com.mangofactory.service.model.ModelRef from);
  public abstract ContainerDataType toSwagger(com.mangofactory.service.model.ContainerDataType from);
  public DataType toSwaggerDataType(com.mangofactory.service.model.DataType from) {
    DataType dataType = new DataType();
    if (from.getDataType() instanceof com.mangofactory.service.model.PrimitiveDataType) {
      dataType.setDataType(toSwagger((com.mangofactory.service.model.PrimitiveDataType)from.getDataType()));
    } else if (from.getDataType()  instanceof com.mangofactory.service.model.PrimitiveFormatDataType) {
      dataType.setDataType(toSwagger((com.mangofactory.service.model.PrimitiveFormatDataType) from.getDataType()));
    } else if (from.getDataType()  instanceof com.mangofactory.service.model.ReferenceDataType) {
      dataType.setDataType(toSwagger((com.mangofactory.service.model.ReferenceDataType) from.getDataType()));
    } else if (from.getDataType()  instanceof com.mangofactory.service.model.ModelRef) {
      dataType.setDataType(toSwagger((com.mangofactory.service.model.ModelRef)from.getDataType()));
    } else if (from.getDataType()  instanceof com.mangofactory.service.model.ContainerDataType) {
      dataType.setDataType(toSwagger((com.mangofactory.service.model.ContainerDataType)from.getDataType()));
    } else {
      return new DataType(from.getAbsoluteType());
    }
    return dataType;
  }

  public com.mangofactory.swagger.dto.SwaggerDataType concreteToSwagger(
          com.mangofactory.service.model.SwaggerDataType from) {

    if (from instanceof com.mangofactory.service.model.PrimitiveDataType) {
      return toSwagger((com.mangofactory.service.model.PrimitiveDataType)from);
    } else if (from instanceof com.mangofactory.service.model.PrimitiveFormatDataType) {
      return toSwagger((com.mangofactory.service.model.PrimitiveFormatDataType)from);
    } else if (from instanceof com.mangofactory.service.model.ReferenceDataType) {
      return toSwagger((com.mangofactory.service.model.ReferenceDataType)from);
    } else if (from instanceof com.mangofactory.service.model.ModelRef) {
      return toSwagger((com.mangofactory.service.model.ModelRef)from);
    } else if (from instanceof com.mangofactory.service.model.ContainerDataType) {
      return toSwagger((com.mangofactory.service.model.ContainerDataType)from);
    } else if (from instanceof com.mangofactory.service.model.DataType) {
      return toSwaggerDataType((com.mangofactory.service.model.DataType)from);
    }
    throw new UnsupportedOperationException();
  }

}
