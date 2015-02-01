package com.mangofactory.documentation.spring.web.scanners;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.service.PathProvider;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Authorization;
import com.mangofactory.documentation.service.model.ResourceGroup;
import com.mangofactory.documentation.service.model.builder.ApiListingBuilder;
import com.mangofactory.documentation.spi.service.contexts.ApiListingContext;
import com.mangofactory.documentation.spi.service.contexts.AuthorizationContext;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

      Set<String> produces = new LinkedHashSet<String>(2);
      Set<String> consumes = new LinkedHashSet<String>(2);
      Set<ApiDescription> apiDescriptions = newHashSet();

      Map<String, Model> models = new LinkedHashMap<String, Model>();
      AuthorizationContext authorizationContext = context.getDocumentationContext().getAuthorizationContext();
      for (RequestMappingContext each : entry.getValue()) {
        models.putAll(apiModelReader.read(each));
        apiDescriptions.addAll(apiDescriptionReader.read(each));
      }

      List<Authorization> authorizations = authorizationContext.getScalaAuthorizations();

      ArrayList sortedDescriptions = new ArrayList(apiDescriptions);
      Collections.sort(sortedDescriptions, context.getDocumentationContext().getApiDescriptionOrdering());

      String resourcePath = longestCommonPath(sortedDescriptions);

      String apiVersion = "1.0";
      PathProvider pathProvider = context.getDocumentationContext().getPathProvider();
      ApiListingBuilder apiListingBuilder = new ApiListingBuilder(context.apiDescriptionOrdering())
              .apiVersion(apiVersion)
              .basePath(pathProvider.getApplicationBasePath())
              .resourcePath(resourcePath)
              .produces(newArrayList(produces))
              .consumes(newArrayList(consumes))
              .protocols(new ArrayList<String>())
              .authorizations(authorizations)
              .apis(sortedDescriptions)
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
