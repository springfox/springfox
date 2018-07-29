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
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.properties.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Header;
import springfox.documentation.service.ResponseMessage;

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

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.swagger2.mappers.ModelMapper.*;

@Mapper(uses = {
    ModelMapper.class,
    ParameterMapper.class,
    SecurityMapper.class,
    LicenseMapper.class,
    VendorExtensionsMapper.class
})
public abstract class ServiceModelToSwagger2Mapper {

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
      @Mapping(target = "vendorExtensions", source = "vendorExtensions")
  })
  public abstract Swagger mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = { LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class }),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions")
  })
  protected abstract Info mapApiInfo(ApiInfo from);

  protected abstract Contact map(springfox.documentation.service.Contact from);

  @Mappings({
      @Mapping(target = "description", source = "notes"),
      @Mapping(target = "operationId", source = "uniqueId"),
      @Mapping(target = "schemes", source = "protocol"),
      @Mapping(target = "security", source = "securityReferences"),
      @Mapping(target = "responses", source = "responseMessages"),
      @Mapping(target = "vendorExtensions", source = "vendorExtensions"),
      @Mapping(target = "externalDocs", ignore = true)
  })
  protected abstract Operation mapOperation(springfox.documentation.service.Operation from);

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

  protected Map<String, Response> mapResponseMessages(Set<ResponseMessage> from) {
    Map<String, Response> responses = new TreeMap<>();
    for (ResponseMessage responseMessage : from) {
      Property responseProperty;
      ModelReference modelRef = responseMessage.getResponseModel();
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

  private Function<Map.Entry<String, Header>, Map.Entry<String, Property>> toPropertyEntry() {
    return entry -> {
      Property property = modelRefToProperty(entry.getValue().getModelReference());
      property.setDescription(entry.getValue().getDescription());
      return new AbstractMap.SimpleEntry<>(entry.getKey(), property);
    };
  }

  protected Map<String, Path> mapApiListings(Map<String, List<ApiListing>> apiListings) {
    Map<String, Path> paths = new TreeMap<>();
    apiListings.values().stream()
        .flatMap(Collection::stream)
        .forEachOrdered(each -> {
          for (ApiDescription api : each.getApis()) {
            paths.put(api.getPath(), mapOperations(api, ofNullable(paths.get(api.getPath()))));
          }
        });
    return paths;
  }

  private Path mapOperations(ApiDescription api, Optional<Path> existingPath) {
    Path path = existingPath.orElse(new Path());
    for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
      Operation operation = mapOperation(each);
      path.set(each.getMethod().toString().toLowerCase(), operation);
    }
    return path;
  }


}
