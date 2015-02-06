package com.mangofactory.documentation.swagger.mappers;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mangofactory.documentation.service.ApiDescription;
import com.mangofactory.documentation.service.ApiInfo;
import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.AuthorizationScope;
import com.mangofactory.documentation.service.Group;
import com.mangofactory.documentation.service.ResponseMessage;
import com.wordnik.swagger.models.Contact;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.License;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Path;
import com.wordnik.swagger.models.Response;
import com.wordnik.swagger.models.Scheme;
import com.wordnik.swagger.models.Swagger;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

@Mapper(uses = {ModelMapper.class, ParameterMapper.class, SecurityMapper.class})
public abstract class ServiceModelToSwagger2Mapper {
  @Mappings({
          @Mapping(target = "swagger", ignore = true),
          @Mapping(target = "info", source = "resourceListing.info"),
          @Mapping(target = "paths", source = "apiListings"),
          @Mapping(target = "basePath", expression = "java(anyApi(from).getBasePath())"),
          @Mapping(target = "host", expression = "java(anyApi(from).getBasePath())"),
          @Mapping(target = "schemes", expression = "java(toSchemes(anyApi(from)))"),
          @Mapping(target = "produces", expression = "java(anyApi(from).getProduces())"),
          @Mapping(target = "consumes", expression = "java(anyApi(from).getConsumes())"),
          @Mapping(target = "parameters", expression = "java(new java.util.HashMap())"),
          @Mapping(target = "definitions", expression = "java(allApiModels(from))"),
          @Mapping(target = "securityDefinitions", expression = "java(toSecuritySchemeDefinitions(anyApi(from)))"),
          @Mapping(target = "externalDocs", ignore = true)
  })
  public abstract Swagger map(com.mangofactory.documentation.service.Group from);

  @Mappings({
          @Mapping(target = "license", expression = "java(toLicense(from))"),
          @Mapping(target = "contact", expression = "java(toContact(from))"),
          @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
          @Mapping(target = "vendorExtensions", ignore = true)
  })
  protected abstract Info map(com.mangofactory.documentation.service.ApiInfo from);

  protected License toLicense(ApiInfo from) {
    return new License().name(from.getLicense()).url(from.getLicenseUrl());
  }

  protected List<Scheme> toSchemes(ApiListing from) {
    return FluentIterable.from(from.getProtocols()).transform(toScheme()).toList();
  }

  protected Contact toContact(ApiInfo from) {
    return new Contact().name(from.getContact());
  }

  @Mappings({
          @Mapping(target = "description", source = "notes"),
          @Mapping(target = "operationId", source = "nickname"),
          @Mapping(target = "schemes", source = "protocol"),
          @Mapping(target = "produces", source = "produces"),
          @Mapping(target = "consumes", source = "consumes"),
          @Mapping(target = "parameters", source = "parameters"),
          @Mapping(target = "security", source = "authorizations"),
          @Mapping(target = "responses", source = "responseMessages"),
          @Mapping(target = "tags", ignore = true),
          @Mapping(target = "vendorExtensions", ignore = true),
          @Mapping(target = "externalDocs", ignore = true)
  })
  protected abstract Operation map(com.mangofactory.documentation.service.Operation from);

  protected List<Map<String, List<String>>> map(
          Map<String, List<com.mangofactory.documentation.service.AuthorizationScope>> from) {
    List<Map<String, List<String>>> security = newArrayList();
    for (Map.Entry<String, List<AuthorizationScope>> each : from.entrySet()) {
      Map<String, List<String>> newEntry = newHashMap();
      newEntry.put(each.getKey(), from(each.getValue()).transform(scopeToString()).toList());
      security.add(newEntry);
    }
    return security;
  }

  protected Function<AuthorizationScope, String> scopeToString() {
    return new Function<AuthorizationScope, String>() {
      @Override
      public String apply(AuthorizationScope input) {
        return input.getScope();
      }
    };
  }

  protected Map<String, Response> map(Set<ResponseMessage> from) {
    HashMap<String, Response> responses = newHashMap();
    for (ResponseMessage responseMessage : from) {
      Response response = new Response()
              .description(responseMessage.getMessage())
              .schema(new RefProperty(responseMessage.getResponseModel()));
      response.setExamples(Maps.<String, String>newHashMap());
      response.setHeaders(Maps.<String, Property>newHashMap());
    }
    return responses;
  }

  protected Map<String, Path> map(Map<String, ApiListing> apiListings) {
    Map<String, Path> paths = newHashMap();
    for (ApiListing each : apiListings.values()) {
      for (ApiDescription api : each.getApis()) {
        paths.put(api.getPath(), map(api));
      }
    }
    return paths;
  }

  protected Path map(ApiDescription api) {
    Path path = new Path();
    for (com.mangofactory.documentation.service.Operation each : api.getOperations()) {
      Operation operation = map(each);
      path.set(each.getMethod(), operation);
    }
    return path;
  }

  protected Map<String, Model> allApiModels(Group group) {
    Map<String, Model> definitions = newHashMap();
    for (ApiListing each : group.getApiListings().values()) {
      definitions.putAll(transformValues(each.getModels(), modelToDto()));
    }
    return definitions;
  }

  protected ApiListing anyApi(Group group) {
    return Iterables.getFirst(group.getApiListings().values(), null);
  }

  private Function<String, Scheme> toScheme() {
    return new Function<String, Scheme>() {
      @Override
      public Scheme apply(String input) {
        return Scheme.forValue(input);
      }
    };
  }

  private Function<com.mangofactory.documentation.schema.Model, Model> modelToDto() {
    return new Function<com.mangofactory.documentation.schema.Model, Model>() {
      @Override
      public Model apply(com.mangofactory.documentation.schema.Model input) {
        return map(input);
      }
    };
  }

}
