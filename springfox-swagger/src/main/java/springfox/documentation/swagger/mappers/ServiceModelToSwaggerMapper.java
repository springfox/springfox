/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.schema.Model;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.swagger.dto.ApiDescription;
import springfox.documentation.swagger.dto.ApiInfo;
import springfox.documentation.swagger.dto.ApiListing;
import springfox.documentation.swagger.dto.ModelDto;
import springfox.documentation.swagger.dto.ModelPropertyDto;
import springfox.documentation.swagger.dto.Operation;
import springfox.documentation.swagger.dto.Parameter;
import springfox.documentation.swagger.dto.ResourceListing;
import springfox.documentation.swagger.dto.ResponseMessage;


@Mapper(uses = {AllowableValuesMapper.class, DataTypeMapper.class, AuthorizationTypesMapper.class})
public interface ServiceModelToSwaggerMapper {
  //Api related
  ApiDescription toSwaggerApiDescription(springfox.documentation.service.ApiDescription from);

  ApiInfo toSwaggerApiInfo(springfox.documentation.service.ApiInfo from);

  @Mappings({
          @Mapping(target = "responseModel", source = "responseModel", qualifiedBy = DataTypeMapper.ResponseTypeName
                  .class)
  })
  ResponseMessage toSwaggerResponseMessage(springfox.documentation.service.ResponseMessage from);

  springfox.documentation.swagger.dto.ApiListingReference toSwaggerApiListingReference(ApiListingReference from);

  ModelDto toSwaggerModelDto(Model from);

  @Mappings({
       @Mapping(target = "swaggerVersion", constant = "1.2"),
       @Mapping(target = "authorizations", source = "securityReferences")
  })
  ApiListing toSwaggerApiListing(springfox.documentation.service.ApiListing from);

  @Mappings({
          @Mapping(target = "type", source = "modelRef", qualifiedBy = DataTypeMapper.Type.class)
  })
  ModelPropertyDto toSwaggerModelPropertyDto(ModelProperty from);

  @Mappings({
      @Mapping(target = "dataType", source = "responseModel", qualifiedBy = DataTypeMapper.OperationType.class),
      @Mapping(target = "authorizations", source = "securityReferences")
  })
  Operation toSwaggerOperation(springfox.documentation.service.Operation from);

  @Mappings({
          @Mapping(target = "parameterType", source = "modelRef", qualifiedBy = DataTypeMapper.OperationType.class)
  })
  Parameter toSwaggerParameter(springfox.documentation.service.Parameter from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2"),
          @Mapping(target = "authorizations", source = "securitySchemes")
  })
  ResourceListing toSwaggerResourceListing(springfox.documentation.service.ResourceListing from);
}
