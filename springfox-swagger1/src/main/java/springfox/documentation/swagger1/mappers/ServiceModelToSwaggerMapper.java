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

package springfox.documentation.swagger1.mappers;

import io.swagger.models.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.swagger1.dto.ApiDescription;
import springfox.documentation.swagger1.dto.ApiInfo;
import springfox.documentation.swagger1.dto.ApiListing;
import springfox.documentation.swagger1.dto.ModelDto;
import springfox.documentation.swagger1.dto.ModelPropertyDto;
import springfox.documentation.swagger1.dto.Operation;
import springfox.documentation.swagger1.dto.Parameter;
import springfox.documentation.swagger1.dto.ResourceListing;
import springfox.documentation.swagger1.dto.ResponseMessage;


@Mapper(uses = {
    AllowableValuesMapper.class,
    DataTypeMapper.class,
    AuthorizationTypesMapper.class
})
@SuppressWarnings("deprecation")
public interface ServiceModelToSwaggerMapper {
  //Api related
  ApiDescription toSwaggerApiDescription(springfox.documentation.service.ApiDescription from);

  @Mappings({
      @Mapping(target = "contact", source = "contact.name")
  })
  ApiInfo toSwaggerApiInfo(springfox.documentation.service.ApiInfo from);

  Contact map(springfox.documentation.service.Contact from);

  @Mappings({
          @Mapping(
              target = "responseModel",
              source = "responseModel",
              qualifiedBy = DataTypeMapper.ResponseTypeName.class)
  })
  ResponseMessage toSwaggerResponseMessage(springfox.documentation.service.ResponseMessage from);

  springfox.documentation.swagger1.dto.ApiListingReference toSwaggerApiListingReference(ApiListingReference from);

  @Mappings({
      @Mapping(target = "subTypes", source = "subTypes", qualifiedBy = DataTypeMapper.ResponseTypeName.class),
  })
  ModelDto toSwaggerModelDto(springfox.documentation.schema.Model from);

  @Mappings({
       @Mapping(target = "swaggerVersion", constant = "1.2"),
       @Mapping(target = "authorizations", source = "securityReferences")
  })
  ApiListing toSwaggerApiListing(springfox.documentation.service.ApiListing from);

  @Mappings({
          @Mapping(target = "type", source = "modelRef", qualifiedBy = DataTypeMapper.Type.class)
  })
  ModelPropertyDto toSwaggerModelPropertyDto(springfox.documentation.schema.ModelProperty from);

  @Mappings({
      @Mapping(target = "dataType", source = "responseModel", qualifiedBy = DataTypeMapper.OperationType.class),
      @Mapping(target = "nickname", source = "uniqueId"),
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
