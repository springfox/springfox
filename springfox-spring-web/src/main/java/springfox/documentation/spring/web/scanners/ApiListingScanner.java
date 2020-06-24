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

package springfox.documentation.spring.web.scanners;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.PathAdjuster;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.paths.PathMappingAdjuster;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.*;
import static java.util.stream.StreamSupport.*;
import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;
import static springfox.documentation.spring.web.paths.Paths.*;
import static springfox.documentation.spring.web.scanners.ResourceGroups.*;

@SuppressWarnings("deprecation")
@Component
public class ApiListingScanner {
  private static final Logger LOGGER = getLogger(ApiListingScanner.class);
  private final ApiDescriptionReader apiDescriptionReader;
  private final ApiModelReader apiModelReader;
  private final ApiModelSpecificationReader modelSpecificationReader;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiListingScanner(
      ApiDescriptionReader apiDescriptionReader,
      ApiModelReader apiModelReader,
      ApiModelSpecificationReader modelSpecificationReader,
      DocumentationPluginsManager pluginsManager) {

    this.apiDescriptionReader = apiDescriptionReader;
    this.apiModelReader = apiModelReader;
    this.modelSpecificationReader = modelSpecificationReader;
    this.pluginsManager = pluginsManager;
  }

  static Optional<String> longestCommonPath(List<ApiDescription> apiDescriptions) {
    List<String> commons = new ArrayList<>();
    if (null == apiDescriptions || apiDescriptions.isEmpty()) {
      return empty();
    }
    List<String> firstWords = urlParts(apiDescriptions.get(0));

    for (int position = 0; position < firstWords.size(); position++) {
      String word = firstWords.get(position);
      boolean allContain = true;
      for (int i = 1; i < apiDescriptions.size(); i++) {
        List<String> words = urlParts(apiDescriptions.get(i));
        if (words.size() < position + 1 || !words.get(position).equals(word)) {
          allContain = false;
          break;
        }
      }
      if (allContain) {
        commons.add(word);
      }
    }
    return of("/" + commons.stream()
        .filter(Objects::nonNull)
        .collect(joining("/")));
  }

  private static List<String> urlParts(ApiDescription apiDescription) {
    return Stream.of(apiDescription.getPath().split("/"))
        .filter(((Predicate<String>) String::isEmpty).negate())
        .map(String::trim)
        .collect(toList());
  }

  public Map<String, List<ApiListing>> scan(ApiListingScanningContext context) {
    final Map<String, List<ApiListing>> apiListingMap = new HashMap<>();
    int position = 0;

    Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup = context
        .getRequestMappingsByResourceGroup();
    Collection<ApiDescription> additionalListings = pluginsManager.additionalListings(context);
    Set<ResourceGroup> allResourceGroups =
        concat(
            stream(
                collectResourceGroups(additionalListings).spliterator(),
                false),
            requestMappingsByResourceGroup.keySet().stream())
            .collect(toSet());

    List<SecurityReference> securityReferences = new ArrayList<>();

    Map<String, Set<springfox.documentation.schema.Model>> globalModelMap = new HashMap<>();
    for (ResourceGroup resourceGroup : sortedByName(allResourceGroups)) {

      DocumentationContext documentationContext = context.getDocumentationContext();
      Set<String> produces = new LinkedHashSet<>(documentationContext.getProduces());
      Set<String> consumes = new LinkedHashSet<>(documentationContext.getConsumes());
      String host = documentationContext.getHost();
      Set<String> protocols = new LinkedHashSet<>(documentationContext.getProtocols());
      Set<ApiDescription> apiDescriptions = new HashSet<>();
      ModelSpecificationRegistryBuilder modelRegistryBuilder = new ModelSpecificationRegistryBuilder();


      final Map<String, springfox.documentation.schema.Model> models = new LinkedHashMap<>();
      List<RequestMappingContext> requestMappings = nullToEmptyList(requestMappingsByResourceGroup.get(resourceGroup));
      for (RequestMappingContext each : sortedByMethods(requestMappings)) {
        Map<String, Set<springfox.documentation.schema.Model>> currentModelMap
            = apiModelReader.read(each.withKnownModels(
            globalModelMap));
        modelRegistryBuilder.addAll(
            modelSpecificationReader.read(each.withKnownModels(globalModelMap))
                .stream()
                .filter(m -> m.key().isPresent())
                .collect(Collectors.toList()));
        currentModelMap.values().forEach(list -> {
          for (springfox.documentation.schema.Model model : list) {
            models.put(
                model.getName(),
                model);
          }
        });
        globalModelMap.putAll(currentModelMap);
        apiDescriptions.addAll(apiDescriptionReader.read(each.withKnownModels(currentModelMap)));
      }

      List<ApiDescription> additional = additionalListings.stream()
          .filter(
              belongsTo(resourceGroup.getGroupName())
                  .and(onlySelectedApis(documentationContext)))
          .collect(toList());

      apiDescriptions.addAll(additional);

      List<ApiDescription> sortedApis = apiDescriptions.stream()
          .sorted(documentationContext.getApiDescriptionOrdering()).collect(toList());

      String resourcePath = new ResourcePathProvider(resourceGroup)
          .resourcePath()
          .orElse(
              longestCommonPath(sortedApis)
                  .orElse(null));


      PathAdjuster adjuster = new PathMappingAdjuster(documentationContext);
      ModelSpecificationRegistry modelRegistry = modelRegistryBuilder.build();
      ModelNamesRegistry modelNamesRegistry =
          pluginsManager.modelNamesGeneratorFactory(documentationContext.getDocumentationType())
              .modelNamesRegistry(modelRegistry);
      LOGGER.trace("Models in the name registry {}", modelNamesRegistry.modelsByName().keySet());
      ApiListingBuilder apiListingBuilder = new ApiListingBuilder(context.apiDescriptionOrdering())
          .apiVersion(documentationContext.getApiInfo().getVersion())
          .basePath(adjuster.adjustedPath(ROOT))
          .resourcePath(resourcePath)
          .produces(produces)
          .consumes(consumes)
          .host(host)
          .protocols(protocols)
          .securityReferences(securityReferences)
          .apis(sortedApis)
          .models(models)
          .modelSpecifications(modelNamesRegistry.modelsByName())
          .modelNamesRegistry(modelNamesRegistry)
          .position(position++)
          .availableTags(documentationContext.getTags());

      ApiListingContext apiListingContext = new ApiListingContext(
          context.getDocumentationType(),
          resourceGroup,
          apiListingBuilder);
      apiListingMap.putIfAbsent(
          resourceGroup.getGroupName(),
          new LinkedList<>());
      apiListingMap.get(resourceGroup.getGroupName()).add(pluginsManager.apiListing(apiListingContext));
    }
    return apiListingMap;
  }

  private Predicate<ApiDescription> onlySelectedApis(final DocumentationContext context) {
    return input -> context.getApiSelector().getPathSelector().test(input.getPath());
  }

  private Iterable<RequestMappingContext> sortedByMethods(List<RequestMappingContext> contexts) {
    return contexts.stream().sorted(methodComparator()).collect(toList());
  }
}