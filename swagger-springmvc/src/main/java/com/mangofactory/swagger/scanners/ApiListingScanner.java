package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.ApiModelReader;
import com.mangofactory.swagger.readers.ApiPathReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.wordnik.swagger.models.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

//import com.wordnik.swagger.core.SwaggerSpec;
//import com.wordnik.swagger.model.ApiDescription;
//import com.wordnik.swagger.model.ApiListing;
//import com.wordnik.swagger.model.Authorization;
//import com.wordnik.swagger.model.Model;

public class ApiListingScanner {
  private static final Logger log = LoggerFactory.getLogger(ApiListingScanner.class);

  private String apiVersion = "1.0";
  //  private String swaggerVersion = SwaggerSpec.version();
  private Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings;
  private SwaggerPathProvider swaggerPathProvider;
  private SwaggerGlobalSettings swaggerGlobalSettings;
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private AuthorizationContext authorizationContext;
  private final ModelProvider modelProvider;
  //  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;

  public ApiListingScanner(Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings,
                           SwaggerPathProvider swaggerPathProvider,
                           ModelProvider modelProvider,
                           AuthorizationContext authorizationContext,
                           Collection<RequestMappingReader> customAnnotationReaders) {

    this.resourceGroupRequestMappings = resourceGroupRequestMappings;
    this.swaggerPathProvider = swaggerPathProvider;
    this.authorizationContext = authorizationContext;
    this.modelProvider = modelProvider;
    this.customAnnotationReaders = customAnnotationReaders;
  }

  public Map<String, Path> scan() {
    Map<String, Path> apiPathMap = newHashMap();
    int position = 0;

    if (null == resourceGroupRequestMappings) {
      log.error("resourceGroupRequestMappings should not be null.");
    } else {

      for (Map.Entry<ResourceGroup, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {
        ResourceGroup resourceGroup = entry.getKey();
        log.info("Scanning api listing for group:[{}]", resourceGroup);
        Set<String> produces = new LinkedHashSet<String>(2);
        Set<String> consumes = new LinkedHashSet<String>(2);
//        Set<ApiDescription> apiDescriptions = newHashSet();
        Set<List<Path>> apiPaths = newHashSet();

        List<Command<RequestMappingContext>> readers = newArrayList();
        readers.add(new MediaTypeReader());
        readers.add(new ApiPathReader(swaggerPathProvider, customAnnotationReaders));
        readers.add(new ApiModelReader(modelProvider));

//        Map<String, Model> models = new LinkedHashMap<String, Model>();
        for (RequestMappingContext each : entry.getValue()) {

          CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
          each.put("authorizationContext", authorizationContext);
          each.put("swaggerGlobalSettings", swaggerGlobalSettings);
          each.put("currentResourceGroup", resourceGroup);
          each.put("modelProvider", modelProvider);

          Map<String, Object> results = commandExecutor.execute(readers, each);

          List<String> producesMediaTypes = (List<String>) results.get("produces");
          List<String> consumesMediaTypes = (List<String>) results.get("consumes");
//          Map<String, Model> swaggerModels = (Map<String, Model>) results.get("models");
//          if (null != swaggerModels) {
//            models.putAll(swaggerModels);
//          }
          produces.addAll(producesMediaTypes);
          consumes.addAll(consumesMediaTypes);


//          List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");


          Map<String, Path> swaggerPaths = (Map<String, Path>) results.get("swaggerPaths");
          log.info("Adding swagger paths: {}", swaggerPaths);
          apiPathMap.putAll(swaggerPaths);

//          path.set("get", new Operation());


//          apiPathMap.put(resourceGroup.getGroupName(), path);

//          apiDescriptions.addAll(apiDescriptionList);
        }

//        List<Path> pathList = newArrayList();

//        scala.collection.immutable.List<Authorization> authorizations = emptyScalaList();
//        if (null != authorizationContext) {
//          authorizations = authorizationContext.getScalaAuthorizations();
//        }

//        Option modelOption = toOption(toScalaModelMap(models));

//        ArrayList sortedDescriptions = new ArrayList(apiDescriptions);
//        Collections.sort(sortedDescriptions, this.apiDescriptionOrdering);

//        String resourcePath = longestCommonPath(sortedDescriptions);

//        ApiListing apiListing = new ApiListing(
//                apiVersion,
//                swaggerVersion,
//                swaggerPathProvider.getApplicationBasePath(),
//                resourcePath,
//                toScalaList(produces),
//                toScalaList(consumes),
//                emptyScalaList(),
//                authorizations,
//                toScalaList(sortedDescriptions),
//                modelOption,
//                toOption(null),
//                position++);

//        apiListingMap.put(resourceGroup.getGroupName(), apiListing);

      }
    }
    return apiPathMap;
  }

//  private String longestCommonPath(ArrayList<ApiDescription> apiDescriptions) {
//    List<String> commons = newArrayList();
//    if (null == apiDescriptions || apiDescriptions.isEmpty()) {
//      return null;
//    }
//    List<String> firstWords = urlParts(apiDescriptions.get(0));
//
//    for (int position = 0; position < firstWords.size(); position++) {
//      String word = firstWords.get(position);
//      boolean allContain = true;
//      for (int i = 1; i < apiDescriptions.size(); i++) {
//        List<String> words = urlParts(apiDescriptions.get(i));
//        if (words.size() < position + 1 || !words.get(position).equals(word)) {
//          allContain = false;
//          break;
//        }
//      }
//      if (allContain) {
//        commons.add(word);
//      }
//    }
//    Joiner joiner = Joiner.on("/").skipNulls();
//    return "/" + joiner.join(commons);
//  }

//  private List<String> urlParts(ApiDescription apiDescription) {
//    List<String> strings = Splitter.on('/')
//            .omitEmptyStrings()
//            .trimResults()
//            .splitToList(apiDescription.path());
//    return strings;
//  }

  public SwaggerGlobalSettings getSwaggerGlobalSettings() {
    return swaggerGlobalSettings;
  }

  public void setSwaggerGlobalSettings(SwaggerGlobalSettings swaggerGlobalSettings) {
    this.swaggerGlobalSettings = swaggerGlobalSettings;
  }

  public void setResourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
  }

  public void setAuthorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
  }

//  public void setApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
//    this.apiDescriptionOrdering = apiDescriptionOrdering;
//  }
}
