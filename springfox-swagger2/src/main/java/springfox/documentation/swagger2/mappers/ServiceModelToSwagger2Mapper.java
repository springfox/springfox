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

package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
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
import springfox.documentation.service.ResponseMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.swagger2.mappers.ModelMapper.*;

@Mapper(uses = { ModelMapper.class, ParameterMapper.class, SecurityMapper.class, LicenseMapper.class,
    VendorExtensionsMapper.class })
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
      @Mapping(target = "vendorExtensions", ignore = true)
  })
  public abstract Swagger mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = { LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class }),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "vendorExtensions", ignore = true)
  })
  protected abstract Info mapApiInfo(ApiInfo from);

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
      @Mapping(target = "vendorExtensions", ignore = true)
  })
  protected abstract Tag mapTag(springfox.documentation.service.Tag from);

  protected List<Scheme> mapSchemes(List<String> from) {
    return from(from).transform(toScheme()).toList();
  }

  protected Contact mapContact(String contact) {
    return new Contact().name(contact);
  }

  protected List<Map<String, List<String>>> mapAuthorizations(
      Map<String, List<AuthorizationScope>> from) {
    List<Map<String, List<String>>> security = newArrayList();
    for (Map.Entry<String, List<AuthorizationScope>> each : from.entrySet()) {
      Map<String, List<String>> newEntry = newHashMap();
      newEntry.put(each.getKey(), from(each.getValue()).transform(scopeToString()).toList());
      security.add(newEntry);
    }
    return security;
  }


  protected Map<String, Response> mapResponseMessages(Set<ResponseMessage> from) {
    HashMap<String, Response> responses = newHashMap();
    for (ResponseMessage responseMessage : from) {
      Property responseProperty;
      ModelReference modelRef = responseMessage.getResponseModel();
      responseProperty = modelRefToProperty(modelRef);
      Response response = new Response()
          .description(responseMessage.getMessage())
          .schema(responseProperty);
      response.setExamples(Maps.<String, Object>newHashMap());
      response.setHeaders(Maps.<String, Property>newHashMap());
      responses.put(String.valueOf(responseMessage.getCode()), response);
    }
    return responses;
  }

  protected Map<String, Path> mapApiListings(Multimap<String, ApiListing> apiListings) {
    Map<String, Path> paths = newHashMap();
    for (ApiListing each : apiListings.values()) {
      for (ApiDescription api : each.getApis()) {
        paths.put(api.getPath(), mapOperations(api, Optional.fromNullable(paths.get(api.getPath()))));
      }
    }
    return paths;
  }

  private Function<AuthorizationScope, String> scopeToString() {
    return new Function<AuthorizationScope, String>() {
      @Override
      public String apply(AuthorizationScope input) {
        return input.getScope();
      }
    };
  }

  private Path mapOperations(ApiDescription api, Optional<Path> existingPath) {
    Path path = existingPath.or(new Path());
    for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
      Operation operation = mapOperation(each);
      path.set(each.getMethod().toString().toLowerCase(), operation);
    }
    return path;
  }


  private Function<String, Scheme> toScheme() {
    return new Function<String, Scheme>() {
      @Override
      public Scheme apply(String input) {
        return Scheme.forValue(input);
      }
    };
  }

}
