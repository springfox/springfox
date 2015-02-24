package com.mangofactory.documentation.swagger.mappers;

import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.swagger.dto.ApiDescription;
import com.mangofactory.documentation.swagger.dto.ApiInfo;
import com.mangofactory.documentation.swagger.dto.ApiListing;
import com.mangofactory.documentation.swagger.dto.ApiListingReference;
import com.mangofactory.documentation.swagger.dto.ModelDto;
import com.mangofactory.documentation.swagger.dto.ModelPropertyDto;
import com.mangofactory.documentation.swagger.dto.Operation;
import com.mangofactory.documentation.swagger.dto.Parameter;
import com.mangofactory.documentation.swagger.dto.ResourceListing;
import com.mangofactory.documentation.swagger.dto.ResponseMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {AllowableValuesMapper.class, DataTypeMapper.class, AuthorizationTypesMapper.class})
public interface ServiceModelToSwaggerMapper {
  //Api related
  ApiDescription toSwaggerApiDescription(com.mangofactory.documentation.service.ApiDescription from);

  ApiInfo toSwaggerApiInfo(com.mangofactory.documentation.service.ApiInfo from);

  @Mappings({
          @Mapping(target = "responseModel",
                  expression = "java(dataTypeMapper.responseTypeName(from.getResponseModel()))")
  })
  ResponseMessage toSwaggerResponseMessage(com.mangofactory.documentation.service.ResponseMessage from);

  ApiListingReference toSwaggerApiListingReference(com.mangofactory.documentation.service.ApiListingReference
                                                           from);

  ModelDto toSwaggerModelDto(Model from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  ApiListing toSwaggerApiListing(com.mangofactory.documentation.service.ApiListing from);

  @Mappings({
          @Mapping(target = "type",
                  expression = "java( dataTypeMapper.typeFromModelRef( from.getModelRef() ) )"),
          @Mapping(target = "items",
                  expression = "java( dataTypeMapper.itemTypeFromModelRef( from.getModelRef() ) )")
  })
  ModelPropertyDto toSwaggerModelPropertyDto(ModelProperty from);

  @Mappings({
          @Mapping(target = "dataType",
                  expression = "java( dataTypeMapper.operationTypeFromModelRef(from.getResponseModel()))")
  })
  Operation toSwaggerOperation(com.mangofactory.documentation.service.Operation from);

  @Mappings({
    @Mapping(target = "parameterType",
      expression = "java( dataTypeMapper.operationTypeFromModelRef( from.getModelRef()))")
  })
  Parameter toSwaggerParameter(com.mangofactory.documentation.service.Parameter from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  ResourceListing toSwaggerResourceListing(com.mangofactory.documentation.service.ResourceListing from);
}
