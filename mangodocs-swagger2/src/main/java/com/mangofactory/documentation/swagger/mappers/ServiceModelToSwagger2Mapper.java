package com.mangofactory.documentation.swagger.mappers;

import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.Operation;
import com.wordnik.swagger.models.Path;
import com.wordnik.swagger.models.Swagger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface ServiceModelToSwagger2Mapper {
  @Mappings({
          @Mapping(target = "info", source = "resourceListing.apiInfo"),
          @Mapping(target = "paths", source = "apiListings")
  })
  Swagger map(com.mangofactory.documentation.service.Group from);


  Path map(ApiListing apiListing, Operation operation);
}
