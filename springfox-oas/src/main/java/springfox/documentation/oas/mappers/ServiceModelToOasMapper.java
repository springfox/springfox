/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.http.HttpMethod;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.MediaType;
import springfox.documentation.service.RequestBody;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static springfox.documentation.builders.BuilderDefaults.*;

@Mapper(componentModel = "spring",
    uses = {
        VendorExtensionsMapper.class,
        LicenseMapper.class,
        ExamplesMapper.class,
        SecurityMapper.class,
        SchemaMapper.class
    })
public abstract class ServiceModelToOasMapper {
  @Mappings({
      @Mapping(target = "openapi", constant = "3.0.0"),
      @Mapping(target = "info", source = "resourceListing.info"),
      @Mapping(target = "externalDocs", source = "documentationReference"),
      @Mapping(target = "security", ignore = true),
      @Mapping(target = "paths", source = "apiListings"),
      @Mapping(target = "components", ignore = true),
      @Mapping(target = "extensions", source = "vendorExtensions")
  })
  public abstract OpenAPI mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "operationId", source = "uniqueId"),
      @Mapping(target = "security", source = "securityReferences"),
      @Mapping(target = "responses", source = "responses"),
      @Mapping(target = "extensions", source = "vendorExtensions"),
      @Mapping(target = "parameters", source = "requestParameters"),
      @Mapping(target = "requestBody", source = "body"),
      @Mapping(target = "description", source = "notes"),
      @Mapping(target = "callbacks", ignore = true),
      @Mapping(target = "servers", ignore = true), //TODO
      @Mapping(target = "externalDocs", ignore = true)
  })
  abstract Operation mapOperation(springfox.documentation.service.Operation from);

  @Mappings({
      @Mapping(target = "schema", source = "parameterSpecification.left"),
      @Mapping(target = "content", source = "parameterSpecification.right"),
      @Mapping(target = "example", ignore = true),
      @Mapping(target = "$ref", ignore = true)
  })
  abstract Parameter mapParameter(springfox.documentation.service.RequestParameter from);

  static Schema fromSimpleParameter(Optional<SimpleParameterSpecification> value) {
    //TODO: Implement this mapping
    return null;
  }

  static Content fromContent(Optional<ContentSpecification> value) {
    //TODO: Implement this mapping
    return null;
  }

  static Schema fromModelSpecification(ModelSpecification model) {
    //TODO: Implement this mapping
    return null;
  }


  static ApiResponses map(java.util.Set<springfox.documentation.service.Response> value) {
    //TODO: Implement this mapping
    return null;
  }

  static io.swagger.v3.oas.models.parameters.RequestBody map(RequestBody from) {
    //TODO: Implement this mapping
    return null;
  }

  Paths mapPaths(Map<String, List<ApiListing>> apiListings) {
    Paths paths = new Paths();
    apiListings.values().stream()
        .flatMap(Collection::stream)
        .forEachOrdered(each -> {
          for (ApiDescription api : each.getApis()) {
            paths.addPathItem(api.getPath(), mapOperations(api, paths.get(api.getPath())));
          }
        });
    return paths;
  }

  private PathItem mapOperations(
      ApiDescription api,
      PathItem existingPath) {
    PathItem path = existingPath;
    if (existingPath == null) {
      path = new PathItem();
    }
    for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
      Operation operation = mapOperation(each);
      path.operation(mapHttpMethod(each.getMethod()), operation);
    }
    return path;
  }

  abstract PathItem.HttpMethod mapHttpMethod(HttpMethod method);

  static Content map(SortedSet<MediaType> value) {
    //TODO: Implement this mapping
    return new Content();
  }

//  ApiResponses mapApiResponses(Set<ResponseMessage> from) {
//    ApiResponses responses = new ApiResponses();
//    for (ResponseMessage responseMessage : from) {
////      Property responseProperty;
////      ModelReference modelRef = responseMessage.getResponseModel();
////      responseProperty = modelRefToProperty(modelRef);
//      ApiResponse response = new ApiResponse()
//          .description(responseMessage.getMessage());
//      Content content = new Content();
//      Map<String, Example> examples = EXAMPLES_MAPPER
//          .mapExamples(responseMessage.getExamples());
//      MediaType item = new MediaType();
//      item.examples(examples);
//      item.encoding()
//      content.addMediaType("application/json", item);
//
//      response.setHeaders(responseMessage.getHeaders().entrySet().stream().map(toPropertyEntry())
//          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
//
//      response.setExtensions();
//      responses.put(String.valueOf(responseMessage.getCode()), response);
//    }
//    return responses;
//  }

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = {LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class}),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "extensions", source = "vendorExtensions")
  })
  protected abstract Info mapApiInfo(ApiInfo from);

  @Mappings({
      @Mapping(target = "extensions", ignore = true)
  })
  protected abstract Contact map(springfox.documentation.service.Contact from);

  @Mappings({
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "extensions", source = "vendorExtensions"),
  })
  protected abstract Tag mapTag(springfox.documentation.service.Tag from);

  @Mappings({
      @Mapping(target = "extensions", source = "extensions")
  })
  protected abstract Server mapServer(springfox.documentation.service.Server from);

  protected ServerVariables serverVariableMap(
      Collection<springfox.documentation.service.ServerVariable> serverVariables) {
    ServerVariables variables = new ServerVariables();
    variables.putAll(serverVariables.stream()
        .collect(Collectors.toMap(
            springfox.documentation.service.ServerVariable::getName, this::mapServerVariable)));
    return variables;
  }

  @Mappings({
      @Mapping(target = "enum", source = "allowedValues"),
      @Mapping(target = "default", source = "defaultValue"),
      @Mapping(target = "_enum", ignore = true),
      @Mapping(target = "_default", ignore = true)
  })
  protected abstract ServerVariable mapServerVariable(springfox.documentation.service.ServerVariable from);

  protected abstract ExternalDocumentation mapExternalDocs(springfox.documentation.service.DocumentationReference from);
}
