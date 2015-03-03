package com.mangofactory.documentation.swagger.web;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.documentation.annotations.ApiIgnore;
import com.mangofactory.documentation.service.Documentation;
import com.mangofactory.documentation.spring.web.GroupCache;
import com.mangofactory.documentation.swagger.dto.ApiListing;
import com.mangofactory.documentation.swagger.dto.ResourceListing;
import com.mangofactory.documentation.swagger.mappers.ServiceModelToSwaggerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.swagger.common.SwaggerPluginSupport.*;
import static com.mangofactory.documentation.swagger.mappers.Mappers.*;

@Controller
public class DefaultSwaggerController {


  @Autowired
  private GroupCache groupCache;

  @Autowired
  private ServiceModelToSwaggerMapper mapper;

  @ApiIgnore
  @RequestMapping(value = {DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<ResourceListing> getResourceListing(
      @RequestParam(value = "group",  required = false) String swaggerGroup) {

    return getSwaggerResourceListing(swaggerGroup);
  }

  @ApiIgnore
  @RequestMapping(value = {DOCUMENTATION_BASE_PATH + "/{swaggerGroup}/{apiDeclaration}"}, method = RequestMethod.GET)
  public
  @ResponseBody
  ResponseEntity<ApiListing> getApiListing(@PathVariable String swaggerGroup, @PathVariable String apiDeclaration) {
    return getSwaggerApiListing(swaggerGroup, apiDeclaration);
  }

  private ResponseEntity<ApiListing> getSwaggerApiListing(String swaggerGroup, String apiDeclaration) {
    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = groupCache.getGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
    }
    Map<String, com.mangofactory.documentation.service.ApiListing> apiListingMap = documentation.getApiListings();
    Map<String, com.mangofactory.documentation.swagger.dto.ApiListing> dtoApiListing
            = transformEntries(apiListingMap, toApiListingDto(mapper));

    ApiListing apiListing = dtoApiListing.get(apiDeclaration);
    return Optional.fromNullable(apiListing)
            .transform(toResponseEntity(ApiListing.class))
            .or(new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND));
  }

  private ResponseEntity<ResourceListing> getSwaggerResourceListing(String swaggerGroup) {
    String groupName = Optional.fromNullable(swaggerGroup).or("default");
    Documentation documentation = groupCache.getGroup(groupName);
    if (documentation == null) {
      return new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
    }
    com.mangofactory.documentation.service.ResourceListing listing = documentation.getResourceListing();
    ResourceListing resourceListing = mapper.toSwaggerResourceListing(listing);
    return Optional.fromNullable(resourceListing)
            .transform(toResponseEntity(ResourceListing.class))
            .or(new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND));
  }

  private <T> Function<T, ResponseEntity<T>> toResponseEntity(Class<T> clazz) {
    return new Function<T, ResponseEntity<T>>() {
      @Override
      public ResponseEntity<T> apply(T input) {
        return new ResponseEntity<T>(input, HttpStatus.OK);
      }
    };
  }
}
