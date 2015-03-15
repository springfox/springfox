package springdox.documentation.swagger.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springdox.documentation.schema.Model;
import springdox.documentation.schema.ModelProperty;
import springdox.documentation.service.ResponseMessage;
import springdox.documentation.swagger.dto.ApiDescription;
import springdox.documentation.swagger.dto.ApiInfo;
import springdox.documentation.swagger.dto.ApiListing;
import springdox.documentation.swagger.dto.ApiListingReference;
import springdox.documentation.swagger.dto.ModelDto;
import springdox.documentation.swagger.dto.ModelPropertyDto;
import springdox.documentation.swagger.dto.Operation;
import springdox.documentation.swagger.dto.Parameter;
import springdox.documentation.swagger.dto.ResourceListing;

import static springdox.documentation.swagger.mappers.DataTypeMapper.*;


@Mapper(uses = {AllowableValuesMapper.class, DataTypeMapper.class, AuthorizationTypesMapper.class})
public interface ServiceModelToSwaggerMapper {
  //Api related
  ApiDescription toSwaggerApiDescription(springdox.documentation.service.ApiDescription from);

  ApiInfo toSwaggerApiInfo(springdox.documentation.service.ApiInfo from);

  @Mappings({
          @Mapping(target = "responseModel", source = "responseModel", qualifiedBy = ResponseTypeName.class)
  })
  springdox.documentation.swagger.dto.ResponseMessage toSwaggerResponseMessage(ResponseMessage from);

  ApiListingReference toSwaggerApiListingReference(springdox.documentation.service.ApiListingReference from);

  ModelDto toSwaggerModelDto(Model from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  ApiListing toSwaggerApiListing(springdox.documentation.service.ApiListing from);

  @Mappings({
          @Mapping(target = "type", source = "modelRef", qualifiedBy = Type.class)
  })
  ModelPropertyDto toSwaggerModelPropertyDto(ModelProperty from);

  @Mappings({
          @Mapping(target = "dataType", source = "responseModel",
                  qualifiedBy = OperationType.class)
  })
  Operation toSwaggerOperation(springdox.documentation.service.Operation from);

  @Mappings({
          @Mapping(target = "parameterType", source = "modelRef", qualifiedBy = OperationType.class)
  })
  Parameter toSwaggerParameter(springdox.documentation.service.Parameter from);

  @Mappings({
          @Mapping(target = "swaggerVersion", constant = "1.2")
  })
  ResourceListing toSwaggerResourceListing(springdox.documentation.service.ResourceListing from);
}
