package com.mangofactory.swagger.scanners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mangofactory.swagger.address.SwaggerAddressProvider;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.readers.ApiModelReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.mangofactory.swagger.readers.RequestMappingOperationReader;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class ApiListingScanner {
  private static final Logger log = LoggerFactory.getLogger(ApiListingScanner.class);

  private String apiVersion = "1.0";
  //  private String swaggerVersion = SwaggerSpec.version();
  private Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings;
  private SwaggerAddressProvider swaggerAddressProvider;
  private SwaggerGlobalSettings swaggerGlobalSettings;
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private AuthorizationContext authorizationContext;
  private final ModelProvider modelProvider;
  //  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;
  private Map<String, Model> swaggerModels;

  public ApiListingScanner(Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings,
                           SwaggerAddressProvider swaggerAddressProvider,
                           ModelProvider modelProvider,
                           AuthorizationContext authorizationContext,
                           Collection<RequestMappingReader> customAnnotationReaders) {

    this.resourceGroupRequestMappings = resourceGroupRequestMappings;
    this.swaggerAddressProvider = swaggerAddressProvider;
    this.authorizationContext = authorizationContext;
    this.modelProvider = modelProvider;
    this.customAnnotationReaders = customAnnotationReaders;
    this.swaggerModels = new LinkedHashMap<String, Model>();
  }

  public Map<String, Path> scan() {
    Map<String, Path> apiPathMap = newHashMap();
    if (null == resourceGroupRequestMappings) {
      log.error("resourceGroupRequestMappings should not be null.");
    } else {

      for (Map.Entry<ResourceGroup, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {
        ResourceGroup resourceGroup = entry.getKey();
        log.info("Scanning api listing for group:[{}]", resourceGroup);

        Set<String> produces = new LinkedHashSet<String>(2);
        Set<String> consumes = new LinkedHashSet<String>(2);

        List<Command<RequestMappingContext>> readers = newArrayList();
        readers.add(new MediaTypeReader());
        readers.add(new RequestMappingOperationReader(swaggerAddressProvider, customAnnotationReaders));
        readers.add(new ApiModelReader(modelProvider));

        Multimap<String, Map<RequestMethod, Operation>> resourceGroupOperations = ArrayListMultimap.create();

        for (RequestMappingContext requestMappingInfo : entry.getValue()) {
          CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
          requestMappingInfo.put("authorizationContext", authorizationContext);
          requestMappingInfo.put("swaggerGlobalSettings", swaggerGlobalSettings);
          requestMappingInfo.put("currentResourceGroup", resourceGroup);
          requestMappingInfo.put("modelProvider", modelProvider);

          Map<String, Object> results = commandExecutor.execute(readers, requestMappingInfo);

          addMediaTypes(produces, consumes, results);
          addModels(results);

          Map<String, Map<RequestMethod, Operation>> requestMappingOperations
                  = (Map<String, Map<RequestMethod, Operation>>) results.get("requestMappingOperations");
          for (Map.Entry<String, Map<RequestMethod, Operation>> opEntry : requestMappingOperations.entrySet()) {
            resourceGroupOperations.put(opEntry.getKey(), opEntry.getValue());
          }
        }
        addOperationsToSwaggerPaths(apiPathMap, resourceGroupOperations);
      }
    }
    return apiPathMap;
  }

  private void addModels(Map<String, Object> results) {
    Map<String, Model> models = (Map<String, Model>) results.get("models");
    if (null != models) {
      swaggerModels.putAll(models);
    }
  }

  private void addMediaTypes(Set<String> produces, Set<String> consumes, Map<String, Object> results) {
    List<String> producesMediaTypes = (List<String>) results.get("produces");
    List<String> consumesMediaTypes = (List<String>) results.get("consumes");
    produces.addAll(producesMediaTypes);
    consumes.addAll(consumesMediaTypes);
  }

  private void addOperationsToSwaggerPaths(Map<String, Path> apiPathMap, Multimap<String, Map<RequestMethod,
          Operation>> requestMappingOperations) {
    for (Map.Entry<String, Map<RequestMethod, Operation>> requestOperation : requestMappingOperations.entries()) {
      String url = requestOperation.getKey();
      Map<RequestMethod, Operation> operations = requestOperation.getValue();
      for (Map.Entry<RequestMethod, Operation> mappingEntry : operations.entrySet()) {
        Path swaggerPath = existingOrNewPath(url, apiPathMap);
        String method = mappingEntry.getKey().toString().toLowerCase();
        Operation operation = mappingEntry.getValue();
        swaggerPath.set(method, operation);
      }
    }
  }

  private Path existingOrNewPath(String url, Map<String, Path> apiPathMap) {
    Path path = apiPathMap.get(url);
    if (null == path) {
      path = new Path();
      apiPathMap.put(url, path);
    }
    return path;
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

  public Map<String, Model> getSwaggerModels() {
    return swaggerModels;
  }

//  public void setApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
//    this.apiDescriptionOrdering = apiDescriptionOrdering;
//  }
}
