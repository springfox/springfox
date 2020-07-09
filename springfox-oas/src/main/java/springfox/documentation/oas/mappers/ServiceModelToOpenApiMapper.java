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
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.ElementFacet;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.Representation;
import springfox.documentation.service.RequestBody;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@Mapper(componentModel = "spring",
    uses = {
        VendorExtensionsMapper.class,
        LicenseMapper.class,
        ExamplesMapper.class,
        SecurityMapper.class,
        SchemaMapper.class,
        StyleEnumMapper.class,
        SecuritySchemeMapper.class
    })
public abstract class ServiceModelToOpenApiMapper {
  private static final Logger LOGGER = getLogger(ServiceModelToOpenApiMapper.class);

  @Mappings({
      @Mapping(target = "openapi", constant = "3.0.3"),
      @Mapping(target = "info", source = "resourceListing.info"),
      @Mapping(target = "externalDocs", source = "externalDocumentation"),
      @Mapping(target = "security", ignore = true),
      @Mapping(target = "paths", source = "apiListings", qualifiedByName = {"PathsMapping"}),
      @Mapping(target = "components.schemas", source = "apiListings", qualifiedByName = {"ModelsMapping"}),
      @Mapping(target = "components.securitySchemes", source = "resourceListing.securitySchemes"),
      @Mapping(target = "extensions", source = "vendorExtensions")
  })
  public abstract OpenAPI mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "operationId", source = "uniqueId"),
      @Mapping(target = "security", source = "securityReferences"),
      @Mapping(target = "extensions", source = "vendorExtensions"),
      @Mapping(target = "parameters", source = "queryParameters"),
      @Mapping(target = "requestBody", source = "body"),
      @Mapping(target = "description", source = "notes"),
      @Mapping(target = "callbacks", ignore = true),
      @Mapping(target = "servers", ignore = true),
      @Mapping(target = "externalDocs", ignore = true)
  })
  abstract Operation mapOperation(
      springfox.documentation.service.Operation from,
      @Context ModelNamesRegistry modelNamesRegistry);

  @Mappings({
      @Mapping(target = "schema", source = "parameterSpecification.query"),
      @Mapping(target = "content", ignore = true),
      @Mapping(target = "example", ignore = true),
      @Mapping(target = "in", source = "in.in"),
      @Mapping(target = "allowEmptyValue",
          expression =
              "java(from.getParameterSpecification().getQuery()"
                  + ".map(q -> q.getAllowEmptyValue())"
                  + ".orElse(null))"),
      @Mapping(target = "style", expression = "java(from.getParameterSpecification().getQuery()"
          + ".map(q -> parameterStyle(q.getStyle()))"
          + ".orElse(null))"),
      @Mapping(target = "explode",
          expression = "java(from.getParameterSpecification().getQuery()"
              + ".map(q -> q.getExplode())"
              + ".orElse(null))"),
      @Mapping(target = "allowReserved",
          expression =
              "java(from.getParameterSpecification().getQuery()"
                  + ".map(q -> q.getAllowReserved())"
                  + ".orElse(null))"),
      @Mapping(target = "$ref", ignore = true)
  })
  abstract Parameter mapParameter(
      RequestParameter from,
      @Context ModelNamesRegistry modelNamesRegistry);

  @SuppressWarnings("unchecked")
  @AfterMapping
  public void afterMappingParameter(
      RequestParameter from,
      @MappingTarget Parameter target) {
    from.getParameterSpecification().getQuery().ifPresent(query -> {
      for (ElementFacet facet : query.getFacets()) {
        if (facet instanceof NumericElementFacet) {
          target.getSchema().maximum(((NumericElementFacet) facet).getMaximum());
          target.getSchema().minimum(((NumericElementFacet) facet).getMinimum());
          target.getSchema().exclusiveMaximum(((NumericElementFacet) facet).getExclusiveMaximum());
          target.getSchema().exclusiveMinimum(((NumericElementFacet) facet).getExclusiveMinimum());
        } else if (facet instanceof EnumerationFacet) {
          target.getSchema().setEnum(((EnumerationFacet) facet).getAllowedValues());
        } else if (facet instanceof StringElementFacet) {
          target.getSchema().setPattern(((StringElementFacet) facet).getPattern());
          target.getSchema().setMaxLength(((StringElementFacet) facet).getMaxLength());
          target.getSchema().setMinLength(((StringElementFacet) facet).getMinLength());
        } else if (facet instanceof CollectionElementFacet) {
          target.getSchema().minItems(((CollectionElementFacet) facet).getMinItems());
          target.getSchema().maxItems(((CollectionElementFacet) facet).getMaxItems());
          target.getSchema().uniqueItems(((CollectionElementFacet) facet).getUniqueItems());
        }
      }
    });
  }

  protected Parameter.StyleEnum parameterStyle(ParameterStyle from) {
    if (from != null) {
      return Parameter.StyleEnum.valueOf(from.name());
    }
    return null;
  }

  protected Schema fromSimpleParameter(
      Optional<SimpleParameterSpecification> value,
      @Context ModelNamesRegistry modelNamesRegistry) {
    return value.map(s -> Mappers.getMapper(SchemaMapper.class)
        .mapModel(s.getModel(), modelNamesRegistry))
        .orElse(null);
  }

  protected ApiResponses map(
      Set<springfox.documentation.service.Response> from,
      @Context ModelNamesRegistry modelNamesRegistry) {
    ApiResponses responses = new ApiResponses();
    for (springfox.documentation.service.Response each : from) {
      ApiResponse response = new ApiResponse()
          .description(each.getDescription());
      Content content = new Content();
      ExamplesMapper exampleMapper = Mappers.getMapper(ExamplesMapper.class);
      MultiValueMap<String, Example> examplesByMediaType
          = new LinkedMultiValueMap<>();
      for (Example example : each.getExamples()) {
        examplesByMediaType.add(example.getMediaType().orElse("*/*"), example);
      }
      Map<String, Representation> representations
          = each.getRepresentations().stream()
          .collect(Collectors.toMap(
              e -> e.getMediaType().toString(),
              Function.identity(),
              (o1, o2) -> o1, TreeMap::new));
      Set<String> mediaTypes = new HashSet<>(representations.keySet());
      mediaTypes.addAll(examplesByMediaType.keySet());
      for (String eachMediaType : mediaTypes) {
        MediaType mediaType = fromRepresentation(
            representations.getOrDefault(eachMediaType, null),
            modelNamesRegistry);
        if (mediaType == null) {
          mediaType = new MediaType();
        }
        mediaType.examples(exampleMapper.mapExamples(nullToEmptyList(examplesByMediaType.get(eachMediaType))));
        content.addMediaType(
            eachMediaType,
            mediaType);
      }
      response.setContent(content);
      response.setHeaders(fromHeaders(each.getHeaders(), modelNamesRegistry));
      new VendorExtensionsMapper()
          .mapExtensions(each.getVendorExtensions())
          .forEach(response::addExtension);
      responses.put(String.valueOf(each.getCode()), response);
    }
    return responses;
  }

  protected io.swagger.v3.oas.models.parameters.RequestBody map(
      RequestBody from,
      @Context ModelNamesRegistry modelNamesRegistry) {
    if (from == null) {
      return null;
    }
    io.swagger.v3.oas.models.parameters.RequestBody mapped = new io.swagger.v3.oas.models.parameters.RequestBody();
    Content content = new Content();
    for (Representation representation : from.getRepresentations()) {
      content.addMediaType(
          representation.getMediaType().toString(),
          fromRepresentation(representation, modelNamesRegistry));
    }
    mapped.content(content);
    return mapped;
  }

  @Named("PathsMapping")
  Paths mapPaths(Map<String, List<ApiListing>> apiListings) {
    Paths paths = new Paths();
    apiListings.values()
        .stream()
        .flatMap(Collection::stream)
        .forEachOrdered(each -> {
          for (ApiDescription api : each.getApis()) {
            paths.addPathItem(
                api.getPath(),
                mapOperations(
                    api,
                    paths.get(api.getPath()),
                    each.getModelNamesRegistry()));
          }
        });
    return paths;
  }

  private PathItem mapOperations(
      ApiDescription api,
      PathItem existingPath,
      ModelNamesRegistry modelNamesRegistry) {
    PathItem path = existingPath;
    if (existingPath == null) {
      path = new PathItem();
    }
    for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
      LOGGER.debug("Mapping operation {}", api.getPath());
      Operation operation = mapOperation(each, modelNamesRegistry);
      path.operation(
          mapHttpMethod(each.getMethod()),
          operation);
    }
    return path;
  }

  abstract PathItem.HttpMethod mapHttpMethod(HttpMethod method);

  private Content map(
      SortedSet<Representation> value,
      ModelNamesRegistry modelNamesRegistry) {
    Content content = new Content();
    for (Representation each : value) {
      content.addMediaType(each.getMediaType().toString(), fromRepresentation(each, modelNamesRegistry));
    }
    return content;
  }

  @Mappings({
      @Mapping(target = "schema", source = "model", qualifiedByName = "ModelsMapping"),
      @Mapping(target = "encoding", source = "encodings"),
      @Mapping(target = "examples", ignore = true),
      @Mapping(target = "example", ignore = true),
      @Mapping(target = "extensions", source = "model.facetExtensions")
  })
  protected abstract MediaType fromRepresentation(
      Representation each,
      @Context ModelNamesRegistry modelNamesRegistry);

  protected Map<String, Encoding> fromEncodings(
      Collection<springfox.documentation.service.Encoding> encodings,
      @Context ModelNamesRegistry namesRegistry) {
    return encodings.stream()
        .collect(Collectors.toMap(
            springfox.documentation.service.Encoding::getPropertyRef,
            e -> mapEncoding(e, namesRegistry)));
  }

  @Mappings({
      @Mapping(target = "style", source = "style", qualifiedByName = {
          "StyleEnumSelector",
          "EncodingStyleEnum"})
  })
  protected abstract Encoding mapEncoding(
      springfox.documentation.service.Encoding from,
      @Context ModelNamesRegistry modelNamesRegistry);

  protected Map<String, Header> fromHeaders(
      Collection<springfox.documentation.service.Header> headers,
      @Context ModelNamesRegistry modelNamesRegistry) {
    return headers.stream()
        .collect(Collectors.toMap(
            springfox.documentation.service.Header::getName,
            h -> mapHeader(h, modelNamesRegistry)));
  }

  //
  @Mappings({
      @Mapping(target = "style", ignore = true),
      @Mapping(target = "deprecated", ignore = true),
      @Mapping(target = "explode", ignore = true),
      @Mapping(target = "schema", source = "modelSpecification"),
      @Mapping(target = "required", source = "required"),
      @Mapping(target = "examples", ignore = true),
      @Mapping(target = "example", ignore = true),
      @Mapping(target = "content", ignore = true),
      @Mapping(target = "extensions", ignore = true),
      @Mapping(target = "$ref", ignore = true),
  })
  protected abstract Header mapHeader(
      springfox.documentation.service.Header from,
      @Context ModelNamesRegistry modelNamesRegistry);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = {
              LicenseMapper.LicenseTranslator.class,
              LicenseMapper.License.class}),
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
            springfox.documentation.service.ServerVariable::getName,
            this::mapServerVariable)));
    return variables;
  }

  @Mappings({
      @Mapping(target = "enum", source = "allowedValues"),
      @Mapping(target = "default", source = "defaultValue"),
      @Mapping(target = "_enum", ignore = true),
      @Mapping(target = "_default", ignore = true)
  })
  protected abstract ServerVariable mapServerVariable(springfox.documentation.service.ServerVariable from);

  protected abstract ExternalDocumentation mapExternalDocs(springfox.documentation.common.ExternalDocumentation from);
}
