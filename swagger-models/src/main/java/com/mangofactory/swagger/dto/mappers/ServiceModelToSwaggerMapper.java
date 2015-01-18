package com.mangofactory.swagger.dto.mappers;

import com.mangofactory.swagger.dto.ApiDescription;
import com.mangofactory.swagger.dto.ApiInfo;
import com.mangofactory.swagger.dto.ApiListing;
import com.mangofactory.swagger.dto.ApiListingReference;
import com.mangofactory.swagger.dto.ModelDto;
import com.mangofactory.swagger.dto.ModelPropertyDto;
import com.mangofactory.swagger.dto.Operation;
import com.mangofactory.swagger.dto.Parameter;
import com.mangofactory.swagger.dto.ResourceListing;
import com.mangofactory.swagger.dto.ResponseMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {AllowableValuesMapper.class, DataTypeMapper.class, AuthorizationTypesMapper.class})
public interface ServiceModelToSwaggerMapper {
  //Api related
  public ApiDescription toSwaggerApiDescription(com.mangofactory.service.model.ApiDescription from);
  public ApiInfo toSwaggerApiInfo(com.mangofactory.service.model.ApiInfo from);
  public ResponseMessage toSwaggerResponseMessage(com.mangofactory.service.model.ResponseMessage from);
  public ApiListingReference toSwaggerApiListingReference(com.mangofactory.service.model.ApiListingReference from);
  public ModelDto toSwaggerModelDto(com.mangofactory.service.model.Model from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  public ApiListing toSwaggerApiListing(com.mangofactory.service.model.ApiListing from);

  @Mappings({
          @Mapping(target = "type",
                  expression = "java( dataTypeMapper.fromResolvedType( from.getType() ) )"),
          @Mapping(target = "items",
                  expression = "java( dataTypeMapper.fromModelRef( from.getItems() ) )")
  })
  public ModelPropertyDto toSwaggerModelPropertyDto(com.mangofactory.service.model.ModelProperty from);

  @Mappings({
          @Mapping(target = "dataType",
                  expression = "java( new com.mangofactory.swagger.dto.DataType( from.getResponseClass() ) )")
  })
  public Operation toSwaggerOperation(com.mangofactory.service.model.Operation from);

  @Mappings({
          @Mapping(target = "parameterType",
                  expression = "java( new com.mangofactory.swagger.dto.DataType( from.getParameterType() ) )")
  })
  public Parameter toSwaggerParameter(com.mangofactory.service.model.Parameter from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  public ResourceListing toSwaggerResourceListing(com.mangofactory.service.model.ResourceListing from);
}
