package com.mangofactory.swagger.scanners;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiListing;
import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.Model;
import com.mangofactory.service.model.builder.ApiListingBuilder;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.core.ApiListingScanningContext;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.ApiDescriptionReader;
import com.mangofactory.swagger.readers.ApiModelReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger LOG = LoggerFactory.getLogger(ApiListingScanner.class);
  private final MediaTypeReader mediaTypeReader;
  private final ApiDescriptionReader apiDescriptionReader;
  private final ApiModelReader apiModelReader;

  @Autowired
  public ApiListingScanner(MediaTypeReader mediaTypeReader,
      ApiDescriptionReader apiDescriptionReader,
      ApiModelReader apiModelReader) {
    this.mediaTypeReader = mediaTypeReader;
    this.apiDescriptionReader = apiDescriptionReader;
    this.apiModelReader = apiModelReader;
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

      List<Command<RequestMappingContext>> readers = newArrayList();
      readers.add(mediaTypeReader);
      readers.add(apiDescriptionReader);
      readers.add(apiModelReader);

      Map<String, Model> models = new LinkedHashMap<String, Model>();
      AuthorizationContext authorizationContext = context.getDocumentationContext().getAuthorizationContext();
      for (RequestMappingContext each : entry.getValue()) {

        CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
        each.put("authorizationContext", authorizationContext);
        each.put("currentResourceGroup", resourceGroup);

        Map<String, Object> results = commandExecutor.execute(readers, each);

        List<String> producesMediaTypes = (List<String>) results.get("produces");
        List<String> consumesMediaTypes = (List<String>) results.get("consumes");
        models.putAll(each.getModelMap());
        produces.addAll(producesMediaTypes);
        consumes.addAll(consumesMediaTypes);

        List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");
        apiDescriptions.addAll(apiDescriptionList);
      }

      List<Authorization> authorizations = authorizationContext.getScalaAuthorizations();

      ArrayList sortedDescriptions = new ArrayList(apiDescriptions);
      Collections.sort(sortedDescriptions, context.getDocumentationContext().getApiDescriptionOrdering());

      String resourcePath = longestCommonPath(sortedDescriptions);

      String apiVersion = "1.0";
      SwaggerPathProvider swaggerPathProvider = context.getDocumentationContext().getSwaggerPathProvider();
      ApiListing apiListing = new ApiListingBuilder()
              .apiVersion(apiVersion)
              .basePath(swaggerPathProvider.getApplicationBasePath())
              .resourcePath(resourcePath)
              .produces(Lists.newArrayList(produces))
              .consumes(Lists.newArrayList(consumes))
              .protocol(new ArrayList<String>())
              .authorizations(authorizations)
              .apis(sortedDescriptions)
              .models(models)
              .description(null)
              .position(position++)
              .build();

      apiListingMap.put(resourceGroup.getGroupName(), apiListing);
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
