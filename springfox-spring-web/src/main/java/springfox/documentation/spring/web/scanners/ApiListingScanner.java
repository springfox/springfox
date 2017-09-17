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

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

@Component
public class ApiListingScanner {
  private final ApiDescriptionReader apiDescriptionReader;
  private final ApiModelReader apiModelReader;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiListingScanner(ApiDescriptionReader apiDescriptionReader,
                           ApiModelReader apiModelReader,
                           DocumentationPluginsManager pluginsManager) {
    this.apiDescriptionReader = apiDescriptionReader;
    this.apiModelReader = apiModelReader;
    this.pluginsManager = pluginsManager;
  }

  public Multimap<String, ApiListing> scan(ApiListingScanningContext context) {
    Multimap<String, ApiListing> apiListingMap = LinkedListMultimap.create();
    int position = 0;

    Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup
            = context.getRequestMappingsByResourceGroup();
    List<SecurityReference> securityReferences = newArrayList();
    for (ResourceGroup resourceGroup : sortedByName(requestMappingsByResourceGroup.keySet())) {

      DocumentationContext documentationContext = context.getDocumentationContext();
      Set<String> produces = new LinkedHashSet<String>(documentationContext.getProduces());
      Set<String> consumes = new LinkedHashSet<String>(documentationContext.getConsumes());
      String host = documentationContext.getHost();
      Set<String> protocols = new LinkedHashSet<String>(documentationContext.getProtocols());
      Set<ApiDescription> apiDescriptions = newHashSet();

      Map<String, Model> models = new LinkedHashMap<String, Model>();
      for (RequestMappingContext each : sortedByMethods(requestMappingsByResourceGroup.get(resourceGroup))) {
        models.putAll(apiModelReader.read(each.withKnownModels(models)));
        apiDescriptions.addAll(apiDescriptionReader.read(each));
      }

      apiDescriptions.addAll(from(pluginsManager.additionalListings(context))
          .filter(onlySelectedApis(documentationContext))
          .toList());

      List<ApiDescription> sortedApis = newArrayList(apiDescriptions);
      Collections.sort(sortedApis, documentationContext.getApiDescriptionOrdering());

      String resourcePath = new ResourcePathProvider(resourceGroup)
          .resourcePath()
          .or(longestCommonPath(sortedApis))
          .orNull();

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
      apiListingMap.put(resourceGroup.getGroupName(), pluginsManager.apiListing(apiListingContext));
    }
    return apiListingMap;
  }

  private Predicate<ApiDescription> onlySelectedApis(final DocumentationContext context) {
    return new Predicate<ApiDescription>() {
      @Override
      public boolean apply(ApiDescription input) {
        return context.getApiSelector().getPathSelector().apply(input.getPath());
      }
    };
  }

  private Iterable<ResourceGroup> sortedByName(Set<ResourceGroup> resourceGroups) {
    return from(resourceGroups).toSortedList(resourceGroupComparator());
  }

  private Iterable<RequestMappingContext> sortedByMethods(List<RequestMappingContext> contexts) {
    return from(contexts).toSortedList(methodComparator());
  }

  static Optional<String> longestCommonPath(List<ApiDescription> apiDescriptions) {
    List<String> commons = newArrayList();
    if (null == apiDescriptions || apiDescriptions.isEmpty()) {
      return Optional.absent();
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
    Joiner joiner = Joiner.on("/").skipNulls();
    return Optional.of("/" + joiner.join(commons));
  }

  static List<String> urlParts(ApiDescription apiDescription) {
    return Splitter.on('/')
            .omitEmptyStrings()
            .trimResults()
            .splitToList(apiDescription.getPath());
  }

}
