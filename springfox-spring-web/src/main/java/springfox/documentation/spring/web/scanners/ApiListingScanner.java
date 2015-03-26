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

package springfox.documentation.spring.web.scanners;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Authorization;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.AuthorizationContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

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

  @SuppressWarnings("unchecked")
  public Map<String, ApiListing> scan(ApiListingScanningContext context) {
    Map<String, ApiListing> apiListingMap = newHashMap();
    int position = 0;

    Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup
            = context.getRequestMappingsByResourceGroup();
    for (Map.Entry<ResourceGroup, List<RequestMappingContext>> entry : requestMappingsByResourceGroup.entrySet()) {

      ResourceGroup resourceGroup = entry.getKey();

      DocumentationContext documentationContext = context.getDocumentationContext();
      Set<String> produces = new LinkedHashSet<String>(documentationContext.getProduces());
      Set<String> consumes = new LinkedHashSet<String>(documentationContext.getConsumes());
      Set<String> protocols = new LinkedHashSet<String>(documentationContext.getProtocols());
      Set<ApiDescription> apiDescriptions = newHashSet();

      Map<String, Model> models = new LinkedHashMap<String, Model>();
      AuthorizationContext authorizationContext = documentationContext.getAuthorizationContext();
      for (RequestMappingContext each : entry.getValue()) {
        models.putAll(apiModelReader.read(each));
        apiDescriptions.addAll(apiDescriptionReader.read(each));
      }

      List<Authorization> authorizations = authorizationContext.getScalaAuthorizations();

      ArrayList sortedApis = new ArrayList(apiDescriptions);
      Collections.sort(sortedApis, documentationContext.getApiDescriptionOrdering());

      String resourcePath = longestCommonPath(sortedApis);

      PathProvider pathProvider = documentationContext.getPathProvider();
      ApiListingBuilder apiListingBuilder = new ApiListingBuilder(context.apiDescriptionOrdering())
              .apiVersion(documentationContext.getApiInfo().getVersion())
              .basePath(pathProvider.getApplicationBasePath())
              .resourcePath(resourcePath)
              .produces(produces)
              .consumes(consumes)
              .protocols(protocols)
              .authorizations(authorizations)
              .apis(sortedApis)
              .models(models)
              .description(null)
              .position(position++);

      ApiListingContext apiListingContext = new ApiListingContext(context.getDocumentationType(), resourceGroup,
              apiListingBuilder);

      apiListingMap.put(resourceGroup.getGroupName(), pluginsManager.apiListing(apiListingContext));
    }
    return apiListingMap;
  }


  static String longestCommonPath(ArrayList<ApiDescription> apiDescriptions) {
    List<String> commons = newArrayList();
    if (null == apiDescriptions || apiDescriptions.isEmpty()) {
      return null;
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
    return "/" + joiner.join(commons);
  }

  static List<String> urlParts(ApiDescription apiDescription) {
    return Splitter.on('/')
            .omitEmptyStrings()
            .trimResults()
            .splitToList(apiDescription.getPath());
  }

}
