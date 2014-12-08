package com.mangofactory.swagger.scanners;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.RequestMappingEvaluator;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.ApiDescriptionReader;
import com.mangofactory.swagger.readers.ApiModelReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.models.dto.ApiDescription;
import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.Authorization;
import com.mangofactory.swagger.models.dto.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

public class ApiListingScanner {
  private static final Logger log = LoggerFactory.getLogger(ApiListingScanner.class);

  private String swaggerVersion = "1.2";
  private Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings;
  private SwaggerPathProvider swaggerPathProvider;
  private SwaggerGlobalSettings swaggerGlobalSettings;
  private AuthorizationContext authorizationContext;
  private final ModelProvider modelProvider;
  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;
  private final RequestMappingEvaluator requestMappingEvaluator;

  public ApiListingScanner(Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings,
                           SwaggerPathProvider swaggerPathProvider,
                           ModelProvider modelProvider,
                           AuthorizationContext authorizationContext,
                           Collection<RequestMappingReader> customAnnotationReaders,
                           RequestMappingEvaluator requestMappingEvaluator) {

    this.resourceGroupRequestMappings = resourceGroupRequestMappings;
    this.swaggerPathProvider = swaggerPathProvider;
    this.authorizationContext = authorizationContext;
    this.modelProvider = modelProvider;
    this.customAnnotationReaders = customAnnotationReaders;
    this.requestMappingEvaluator = requestMappingEvaluator;
  }

  @SuppressWarnings("unchecked")
  public Map<String, ApiListing> scan() {
    Map<String, ApiListing> apiListingMap = newHashMap();
    int position = 0;

    if (null == resourceGroupRequestMappings) {
      log.error("resourceGroupRequestMappings should not be null.");
    } else {
      for (Map.Entry<ResourceGroup, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {

        ResourceGroup resourceGroup = entry.getKey();

        Set<String> produces = new LinkedHashSet<String>(2);
        Set<String> consumes = new LinkedHashSet<String>(2);
        Set<ApiDescription> apiDescriptions = newHashSet();

        List<Command<RequestMappingContext>> readers = newArrayList();
        readers.add(new MediaTypeReader());
        readers.add(new ApiDescriptionReader(swaggerPathProvider, customAnnotationReaders, requestMappingEvaluator));
        readers.add(new ApiModelReader(modelProvider));

        Map<String, Model> models = new LinkedHashMap<String, Model>();
        for (RequestMappingContext each : entry.getValue()) {

          CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
          each.put("authorizationContext", authorizationContext);
          each.put("swaggerGlobalSettings", swaggerGlobalSettings);
          each.put("currentResourceGroup", resourceGroup);

          Map<String, Object> results = commandExecutor.execute(readers, each);

          List<String> producesMediaTypes = (List<String>) results.get("produces");
          List<String> consumesMediaTypes = (List<String>) results.get("consumes");
          Map<String, Model> swaggerModels = (Map<String, Model>) results.get("models");
          if (null != swaggerModels) {
            models.putAll(swaggerModels);
          }
          produces.addAll(producesMediaTypes);
          consumes.addAll(consumesMediaTypes);

          List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");
          apiDescriptions.addAll(apiDescriptionList);
        }

        List<Authorization> authorizations = new ArrayList<Authorization>();
        if (null != authorizationContext) {
          authorizations = authorizationContext.getScalaAuthorizations();
        }

        ArrayList sortedDescriptions = new ArrayList(apiDescriptions);
        Collections.sort(sortedDescriptions, this.apiDescriptionOrdering);

        String resourcePath = longestCommonPath(sortedDescriptions);

        String apiVersion = "1.0";
        ApiListing apiListing = new ApiListing(
                apiVersion,
                swaggerVersion,
                swaggerPathProvider.getApplicationBasePath(),
                resourcePath,
                Lists.newArrayList(produces),
                Lists.newArrayList(consumes),
                new ArrayList<String>(),
                authorizations,
                sortedDescriptions,
                models,
                null,
                position++);

        apiListingMap.put(resourceGroup.getGroupName(), apiListing);
      }
    }
    return apiListingMap;
  }


  private String longestCommonPath(ArrayList<ApiDescription> apiDescriptions) {
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

  private List<String> urlParts(ApiDescription apiDescription) {
    return Splitter.on('/')
            .omitEmptyStrings()
            .trimResults()
            .splitToList(apiDescription.getPath());
  }

  public SwaggerGlobalSettings getSwaggerGlobalSettings() {
    return swaggerGlobalSettings;
  }

  public void setSwaggerGlobalSettings(SwaggerGlobalSettings swaggerGlobalSettings) {
    this.swaggerGlobalSettings = swaggerGlobalSettings;
  }

  @SuppressWarnings("UnusedParameters")
  @Deprecated //As of 0.9.3 (not used)
  public void setResourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
  }

  public void setAuthorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
  }

  public void setApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
  }
}
