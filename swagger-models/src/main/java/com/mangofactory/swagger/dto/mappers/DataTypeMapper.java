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
  public abstract PrimitiveDataType toSwaggerPrimitiveDataType(com.mangofactory.service.model.PrimitiveDataType from);

  public abstract PrimitiveFormatDataType
  toSwaggerPrimitiveFormatDataType(com.mangofactory.service.model.PrimitiveFormatDataType from);

  public abstract ReferenceDataType toSwaggerReferenceDataType(com.mangofactory.service.model.ReferenceDataType from);

  public abstract ModelRef toSwaggerModelRef(com.mangofactory.service.model.ModelRef from);

  public abstract ContainerDataType toSwaggerContainerDataType(com.mangofactory.service.model.ContainerDataType from);

  public DataType toSwaggerDataType(com.mangofactory.service.model.DataType from) {
    DataType dataType = new DataType();
    if (from.getDataType() instanceof com.mangofactory.service.model.PrimitiveDataType) {
      dataType.setDataType(
              toSwaggerPrimitiveDataType((com.mangofactory.service.model.PrimitiveDataType) from.getDataType()));
    } else if (from.getDataType() instanceof com.mangofactory.service.model.PrimitiveFormatDataType) {
      dataType.setDataType(toSwaggerPrimitiveFormatDataType(
              (com.mangofactory.service.model.PrimitiveFormatDataType) from.getDataType()));
    } else if (from.getDataType() instanceof com.mangofactory.service.model.ReferenceDataType) {
      dataType.setDataType(
              toSwaggerReferenceDataType((com.mangofactory.service.model.ReferenceDataType) from.getDataType()));
    } else if (from.getDataType() instanceof com.mangofactory.service.model.ContainerDataType) {
      dataType.setDataType(
              toSwaggerContainerDataType((com.mangofactory.service.model.ContainerDataType) from.getDataType()));
    }
    return dataType;
  }

  public com.mangofactory.swagger.dto.SwaggerDataType toSwaggerSwaggerDataType(
          com.mangofactory.service.model.SwaggerDataType from) {

    if (from instanceof com.mangofactory.service.model.PrimitiveDataType) {
      return toSwaggerPrimitiveDataType((com.mangofactory.service.model.PrimitiveDataType) from);
    } else if (from instanceof com.mangofactory.service.model.PrimitiveFormatDataType) {
      return toSwaggerPrimitiveFormatDataType((com.mangofactory.service.model.PrimitiveFormatDataType) from);
    } else if (from instanceof com.mangofactory.service.model.ReferenceDataType) {
      return toSwaggerReferenceDataType((com.mangofactory.service.model.ReferenceDataType) from);
    } else if (from instanceof com.mangofactory.service.model.ModelRef) {
      return toSwaggerModelRef((com.mangofactory.service.model.ModelRef) from);
    } else if (from instanceof com.mangofactory.service.model.ContainerDataType) {
      return toSwaggerContainerDataType((com.mangofactory.service.model.ContainerDataType) from);
    } else if (from instanceof com.mangofactory.service.model.DataType) {
      return toSwaggerDataType((com.mangofactory.service.model.DataType) from);
    }
    throw new UnsupportedOperationException();
  }

}
