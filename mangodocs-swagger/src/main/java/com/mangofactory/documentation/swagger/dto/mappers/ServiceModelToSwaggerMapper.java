package com.mangofactory.documentation.swagger.dto.mappers;

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
  public ApiDescription toSwaggerApiDescription(com.mangofactory.documentation.service.model.ApiDescription from);
  public ApiInfo toSwaggerApiInfo(com.mangofactory.documentation.service.model.ApiInfo from);
  public ResponseMessage toSwaggerResponseMessage(com.mangofactory.documentation.service.model.ResponseMessage from);
  public ApiListingReference toSwaggerApiListingReference(com.mangofactory.documentation.service.model.ApiListingReference from);
  public ModelDto toSwaggerModelDto(Model from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  public ApiListing toSwaggerApiListing(com.mangofactory.documentation.service.model.ApiListing from);

  @Mappings({
          @Mapping(target = "type",
                  expression = "java( dataTypeMapper.fromTypeName( from.getTypeName() ) )"),
          @Mapping(target = "items",
                  expression = "java( dataTypeMapper.fromModelRef( from.getItems() ) )")
  })
  public ModelPropertyDto toSwaggerModelPropertyDto(ModelProperty from);

  @Mappings({
          @Mapping(target = "dataType",
                  expression = "java( new com.mangofactory.documentation.swagger.dto.DataType( from.getResponseClass() ) )")
  })
  public Operation toSwaggerOperation(com.mangofactory.documentation.service.model.Operation from);

  @Mappings({
          @Mapping(target = "parameterType",
                  expression = "java( new com.mangofactory.documentation.swagger.dto.DataType( from.getParameterType() ) )")
  })
  public Parameter toSwaggerParameter(com.mangofactory.documentation.service.model.Parameter from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  public ResourceListing toSwaggerResourceListing(com.mangofactory.documentation.service.model.ResourceListing from);
}
