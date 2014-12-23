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

@Mapper(uses = {AllowableValuesMapper.class, DataTypeMapper.class, AuthorizationTypesMapper.class})
public interface ServiceModelToSwaggerMapper {
  //Api related
  public ApiDescription toSwagger(com.mangofactory.service.model.ApiDescription from);
  public ApiInfo toSwagger(com.mangofactory.service.model.ApiInfo from);
  public ApiListing toSwagger(com.mangofactory.service.model.ApiListing from);
  public ApiListingReference toSwagger(com.mangofactory.service.model.ApiListingReference from);
  public ModelDto toSwagger(com.mangofactory.service.model.Model from);
  public ModelPropertyDto toSwagger(com.mangofactory.service.model.ModelProperty from);
  public Operation toSwagger(com.mangofactory.service.model.Operation from);
  public Parameter toSwagger(com.mangofactory.service.model.Parameter from);
  public ResourceListing toSwagger(com.mangofactory.service.model.ResourceListing from);
  public ResponseMessage toSwagger(com.mangofactory.service.model.ResponseMessage from);
}
