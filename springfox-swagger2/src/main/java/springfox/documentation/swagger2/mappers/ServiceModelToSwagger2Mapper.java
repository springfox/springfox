/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger2.mappers;


import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.properties.Property;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Header;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.Representation;
import springfox.documentation.service.RequestParameter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@Mapper(uses = {
    CompatibilityModelMapper.class,
    SecurityMapper.class,
    LicenseMapper.class,
    VendorExtensionsMapper.class
}, componentModel = "spring")
public abstract class ServiceModelToSwagger2Mapper {
  private static final Logger LOGGER = getLogger(ServiceModelToSwagger2Mapper.class);

  @Autowired
  @Value("${springfox.documentation.swagger.use-model-v3:true}")
  @SuppressWarnings("VisibilityModifier")
  boolean useModelV3;

  @Mappings({
      @Mapping(target = "info", source = "resourceListing.info"),
      @Mapping(target = "paths", source = "apiListings"),
      @Mapping(target = "host", source = "host"),
      @Mapping(target = "schemes", source = "schemes"),
      @Mapping(target = "definitions", source = "apiListings"),
      @Mapping(target = "securityDefinitions", source = "resourceListing"),
      @Mapping(target = "securityRequirement", ignore = true),
      @Mapping(target = "security", ignore = true),
      @Mapping(target = "swagger", ignore = true),
      @Mapping(target = "parameters", ignore = true),
      @Mapping(target = "responses", ignore = true),
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions"),
      @Mapping(target = "tag", ignore = true),
      @Mapping(target = "scheme", ignore = true)
  })
  public abstract Swagger mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = {LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class}),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions"),
      @Mapping(target = "mergeWith", ignore = true)
  })
  protected abstract Info mapApiInfo(ApiInfo from);

  protected abstract Contact map(springfox.documentation.service.Contact from);

  @BeforeMapping
  @SuppressWarnings("deprecation")
  void beforeMappingOperations(
      @MappingTarget Operation target,
      springfox.documentation.service.Operation source,
      @Context ModelNamesRegistry modelNamesRegistry) {
    List<io.swagger.models.parameters.Parameter> parameters = new ArrayList<>();
    if (useModelV3) {
      for (RequestParameter each : source.getRequestParameters()) {
        parameters.addAll(Mappers.getMapper(RequestParameterMapper.class)
            .mapParameter(each, modelNamesRegistry));
      }
      target.setResponses(mapResponses(source.getResponses(), modelNamesRegistry));
    } else {
      for (springfox.documentation.service.Parameter each : source.getParameters()) {
        parameters.add(Mappers.getMapper(ParameterMapper.class).mapParameter(each));
      }
      target.setResponses(mapResponseMessages(source.getResponseMessages()));
    }
    target.setParameters(parameters);
  }

  @Mappings({
      @Mapping(target = "description", source = "notes"),
      @Mapping(target = "operationId", source = "uniqueId"),
      @Mapping(target = "schemes", source = "protocol"),
      @Mapping(target = "security", source = "securityReferences"),
      @Mapping(target = "responses", ignore = true),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions"),
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "scheme", ignore = true),
      @Mapping(target = "defaultResponse", ignore = true),
      @Mapping(target = "tag", ignore = true),
      @Mapping(target = "parameter", ignore = true),
      @Mapping(target = "parameters", ignore = true)
  })
  protected abstract Operation mapOperation(
      springfox.documentation.service.Operation from,
      @Context ModelNamesRegistry modelNames);

  @Mappings({
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions")
  })
  protected abstract Tag mapTag(springfox.documentation.service.Tag from);

  protected List<Scheme> mapSchemes(List<String> from) {
    return from.stream().map(Scheme::forValue).collect(toList());
  }

  protected List<Map<String, List<String>>> mapAuthorizations(
      Map<String, List<AuthorizationScope>> from) {
    List<Map<String, List<String>>> security = new ArrayList<>();
    for (Map.Entry<String, List<AuthorizationScope>> each : from.entrySet()) {
      Map<String, List<String>> newEntry = new HashMap<>();
      newEntry.put(each.getKey(), each.getValue().stream().map(AuthorizationScope::getScope).collect(toList()));
      security.add(newEntry);
    }
    return security;
  }


  /**
   * Not required when using {@link ServiceModelToSwagger2Mapper#mapResponses(Set, ModelNamesRegistry)} instead
   *
   * @deprecated @since 3.0.0
   */
  @Deprecated
  protected Map<String, Response> mapResponseMessages(Set<springfox.documentation.service.ResponseMessage> from) {
    Map<String, Response> responses = new TreeMap<>();
    for (springfox.documentation.service.ResponseMessage responseMessage : from) {
      Property responseProperty;
      springfox.documentation.schema.ModelReference modelRef = responseMessage.getResponseModel();
      responseProperty = modelRefToProperty(modelRef);
      Response response = new Response()
          .description(responseMessage.getMessage())
          .schema(responseProperty);
      Map<String, Object> examples = new ExamplesMapper()
          .mapExamples(responseMessage.getExamples());
      response.setExamples(examples);
      response.setHeaders(responseMessage.getHeaders().entrySet().stream().map(toPropertyEntry())
          .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
      Map<String, Object> extensions = new VendorExtensionsMapper()
          .mapExtensions(responseMessage.getVendorExtensions());
      response.getVendorExtensions().putAll(extensions);
      responses.put(String.valueOf(responseMessage.getCode()), response);
    }
    return responses;
  }

  protected Map<String, Response> mapResponses(
      Set<springfox.documentation.service.Response> from,
      ModelNamesRegistry modelNamesRegistry) {
    Map<String, Response> responses = new TreeMap<>();
    for (springfox.documentation.service.Response each : from) {
      Response response = new Response()
          .description(each.getDescription());
      for (Representation representation : each.getRepresentations()) {
        Model responseModel = Mappers.getMapper(ModelSpecificationMapper.class)
            .mapModels(representation.getModel(), modelNamesRegistry);
        Property property = Mappers.getMapper(PropertyMapper.class)
            .fromModel(representation.getModel(), modelNamesRegistry);
        response.setResponseSchema(responseModel);
        response.schema(property);
      }
      response.setHeaders(each.getHeaders().stream()
          .collect(Collectors.toMap(
              Header::getName,
              h -> Mappers.getMapper(PropertyMapper.class)
                  .fromModel(h.getModelSpecification(), modelNamesRegistry))));
      Map<String, Object> extensions = new VendorExtensionsMapper()
          .mapExtensions(each.getVendorExtensions());
      response.getVendorExtensions().putAll(extensions);
      Map<String, Object> examples = new ExamplesMapper()
          .mapExamples(each.getExamples());
      response.setExamples(examples);
      responses.put(String.valueOf(each.getCode()), response);
    }
    return responses;
  }

  private Function<Map.Entry<String, Header>, Map.Entry<String, Property>> toPropertyEntry() {
    return entry -> {
      Property property = modelRefToProperty(entry.getValue().getModelReference());
      property.setDescription(entry.getValue().getDescription());
      return new AbstractMap.SimpleEntry<>(entry.getKey(), property);
    };
  }

  @SuppressWarnings("deprecation")
  private Property modelRefToProperty(springfox.documentation.schema.ModelReference modelReference) {
    return springfox.documentation.swagger2.mappers.ModelMapper.modelRefToProperty(modelReference);
  }

  protected Map<String, Path> mapApiListings(Map<String, List<ApiListing>> apiListings) {
    Map<String, Path> paths = new TreeMap<>();
    apiListings.values().stream()
        .flatMap(Collection::stream)
        .forEachOrdered(each -> {
          for (ApiDescription api : each.getApis()) {
            LOGGER.debug("Mapping operation with path {}", api.getPath());
            paths.put(
                api.getPath(),
                mapOperations(api, ofNullable(paths.get(api.getPath())), each.getModelNamesRegistry()));
          }
        });
    return paths;
  }

  private Path mapOperations(
      ApiDescription api,
      Optional<Path> existingPath,
      ModelNamesRegistry modelNamesRegistry) {
    Path path = existingPath.orElse(new Path());
    for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
      Operation operation = mapOperation(each, modelNamesRegistry);
      path.set(each.getMethod().toString().toLowerCase(), operation);
    }
    return path;
  }
}
