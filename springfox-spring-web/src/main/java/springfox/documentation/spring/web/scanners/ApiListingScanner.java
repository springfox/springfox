/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import static springfox.documentation.spi.service.contexts.Orderings.methodComparator;
import static springfox.documentation.spi.service.contexts.Orderings.resourceGroupComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.PathAdjuster;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.paths.PathMappingAdjuster;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.util.Strings;

@Component
public class ApiListingScanner {
  private final ApiDescriptionReader apiDescriptionReader;
  private final ApiModelReader apiModelReader;
  private final DocumentationPluginsManager pluginsManager;
  
  private final static Pattern SLASH_PATTERN = Pattern.compile("/");

  @Autowired
  public ApiListingScanner(ApiDescriptionReader apiDescriptionReader,
                           ApiModelReader apiModelReader,
                           DocumentationPluginsManager pluginsManager) {
    this.apiDescriptionReader = apiDescriptionReader;
    this.apiModelReader = apiModelReader;
    this.pluginsManager = pluginsManager;
  }

  public Map<String, List<ApiListing>> scan(ApiListingScanningContext context) {
    Map<String, List<ApiListing>> apiListingMap = new LinkedHashMap<>();
    int position = 0;

    Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup
            = context.getRequestMappingsByResourceGroup();
    List<SecurityReference> securityReferences = new ArrayList<>();
    for (ResourceGroup resourceGroup : sortedByName(requestMappingsByResourceGroup.keySet())) {

      DocumentationContext documentationContext = context.getDocumentationContext();
      Set<String> produces = new LinkedHashSet<String>(documentationContext.getProduces());
      Set<String> consumes = new LinkedHashSet<String>(documentationContext.getConsumes());
      String host = documentationContext.getHost();
      Set<String> protocols = new LinkedHashSet<String>(documentationContext.getProtocols());
      Set<ApiDescription> apiDescriptions = new HashSet<>();

      Map<String, Model> models = new LinkedHashMap<String, Model>();
      for (RequestMappingContext each : sortedByMethods(requestMappingsByResourceGroup.get(resourceGroup))) {
        models.putAll(apiModelReader.read(each.withKnownModels(models)));
        apiDescriptions.addAll(apiDescriptionReader.read(each));
      }

      apiDescriptions.addAll(pluginsManager.additionalListings(context).stream()
          .filter(onlySelectedApis(documentationContext))
          .collect(Collectors.toList()));

      List<ApiDescription> sortedApis = new ArrayList<>(apiDescriptions);
      Collections.sort(sortedApis, documentationContext.getApiDescriptionOrdering());

      String resourcePath = new ResourcePathProvider(resourceGroup)
          .resourcePath()
          .orElse(longestCommonPath(sortedApis).orElse(null));

      PathProvider pathProvider = documentationContext.getPathProvider();
      String basePath = pathProvider.getApplicationBasePath();
      PathAdjuster adjuster = new PathMappingAdjuster(documentationContext);
      ApiListingBuilder apiListingBuilder = new ApiListingBuilder(context.apiDescriptionOrdering())
              .apiVersion(documentationContext.getApiInfo().getVersion())
              .basePath(adjuster.adjustedPath(basePath))
              .resourcePath(resourcePath)
              .produces(produces)
              .consumes(consumes)
              .host(host)
              .protocols(protocols)
              .securityReferences(securityReferences)
              .apis(sortedApis)
              .models(models)
              .position(position++)
              .availableTags(documentationContext.getTags());

      ApiListingContext apiListingContext = new ApiListingContext(
          context.getDocumentationType(),
          resourceGroup,
          apiListingBuilder);
      apiListingMap.computeIfAbsent(resourceGroup.getGroupName(), k -> new ArrayList<>()).add(pluginsManager.apiListing(apiListingContext));
    }
    return apiListingMap;
  }

  private Predicate<ApiDescription> onlySelectedApis(final DocumentationContext context) {
    return new Predicate<ApiDescription>() {
      @Override
      public boolean test(ApiDescription input) {
        return context.getApiSelector().getPathSelector().test(input.getPath());
      }
    };
  }

  private List<ResourceGroup> sortedByName(Set<ResourceGroup> resourceGroups) {
    return resourceGroups.stream().sorted(resourceGroupComparator()).collect(Collectors.toList());
  }

  private List<RequestMappingContext> sortedByMethods(List<RequestMappingContext> contexts) {
    return contexts.stream().sorted(methodComparator()).collect(Collectors.toList());
  }

  static Optional<String> longestCommonPath(List<ApiDescription> apiDescriptions) {
    List<String> commons = new ArrayList<>();
    if (null == apiDescriptions || apiDescriptions.isEmpty()) {
      return Optional.empty();
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
    return Optional.of("/" + commons.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/")));
  }

  static List<String> urlParts(ApiDescription apiDescription) {
    return SLASH_PATTERN.splitAsStream(apiDescription.getPath())
        .filter(s -> !Strings.isNullOrEmpty(s))
        .map(String::trim)
        .collect(Collectors.toList());
  }

}
