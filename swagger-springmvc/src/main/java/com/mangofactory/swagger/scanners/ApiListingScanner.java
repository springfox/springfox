package com.mangofactory.swagger.scanners;

import com.google.common.base.Predicate;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.readers.ApiDescriptionReader;
import com.mangofactory.swagger.readers.ApiModelReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.Authorization;
import com.wordnik.swagger.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;
import scala.Option;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiListingScanner {
   private static final Logger log = LoggerFactory.getLogger(ApiListingScanner.class);

   private String apiVersion = "1.0";
   private String swaggerVersion = SwaggerSpec.version();
   private Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings;
   private SwaggerPathProvider swaggerPathProvider;
   private List<Command<RequestMappingContext>> readers = newArrayList();
   private SwaggerGlobalSettings swaggerGlobalSettings;
   private ResourceGroupingStrategy controllerNamingStrategy;
   private AuthorizationContext authorizationContext;
    private final ModelProvider modelProvider;

    public ApiListingScanner(Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings,
                             SwaggerPathProvider swaggerPathProvider, ModelProvider modelProvider,
                             AuthorizationContext authorizationContext) {
      this.resourceGroupRequestMappings = resourceGroupRequestMappings;
      this.swaggerPathProvider = swaggerPathProvider;
      this.authorizationContext = authorizationContext;
      this.modelProvider = modelProvider;
    }

   public Map<String, ApiListing> scan() {
      Map<String, ApiListing> apiListingMap = newHashMap();

      readers = newArrayList();
      readers.add(new MediaTypeReader());
      readers.add(new ApiDescriptionReader(swaggerPathProvider));
      readers.add(new ApiModelReader(modelProvider));

      int position = 0;
      for (Map.Entry<ResourceGroup, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {

         ResourceGroup controllerGroup = entry.getKey();

         Set<String> produces = new LinkedHashSet<String>(2);
         Set<String> consumes = new LinkedHashSet<String>(2);
         Set<ApiDescription> apiDescriptions = newHashSet();

         Map<String, Model> models = new LinkedHashMap<String, Model>();
         for(RequestMappingContext each : entry.getValue()){

            CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
            each.put("authorizationContext", authorizationContext);
            each.put("swaggerGlobalSettings", swaggerGlobalSettings);
            Map<String, Object> results = commandExecutor.execute(readers, each);

            List<String> producesMediaTypes = (List<String>) results.get("produces");
            List<String> consumesMediaTypes = (List<String>) results.get("consumes");
            Map<String, Model> swaggerModels = (Map<String, Model>) results.get("models");
            if(null != swaggerModels){
               models.putAll(swaggerModels);
            }
            produces.addAll(producesMediaTypes);
            consumes.addAll(consumesMediaTypes);

            List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");
            apiDescriptions.addAll(apiDescriptionList);
         }

         String resourcePath = UriComponentsBuilder
               .fromPath(swaggerPathProvider.getApiResourcePrefix())
               .pathSegment(controllerGroup.getGroupName())
               .build()
               .toString();

         scala.collection.immutable.List<Authorization> authorizations = emptyScalaList();
         if (null != authorizationContext) {
            authorizations = authorizationContext.getScalaAuthorizations();
         }

        Option modelOption = toOption(models);
        if(null != models){
          modelOption = toOption(toScalaModelMap(models));
        }

          String groupPrefix = String.format("%s%s", swaggerPathProvider.getApiResourcePrefix(),
                  controllerGroup.getRealUri());
          ApiListing apiListing = new ApiListing(
               apiVersion,
               swaggerVersion,
               swaggerPathProvider.getApplicationBasePath(),
               resourcePath,
               toScalaList(produces),
               toScalaList(consumes),
               emptyScalaList(),
               authorizations,
               toScalaList(filter(apiDescriptions, withPathBeginning(groupPrefix))),
               modelOption,
               toOption(null),
               position++);
         apiListingMap.put(controllerGroup.getGroupName(), apiListing);
      }
      return apiListingMap;
   }

    private Predicate<? super ApiDescription> withPathBeginning(final String path) {
        return new Predicate<ApiDescription>() {
            @Override
            public boolean apply(ApiDescription input) {
                return input.path().toLowerCase().startsWith(path.toLowerCase());
            }
        };
    }

    public SwaggerGlobalSettings getSwaggerGlobalSettings() {
      return swaggerGlobalSettings;
   }

   public void setSwaggerGlobalSettings(SwaggerGlobalSettings swaggerGlobalSettings) {
      this.swaggerGlobalSettings = swaggerGlobalSettings;
   }

   public List<Command<RequestMappingContext>> getReaders() {
      return readers;
   }

   public void setReaders(List<Command<RequestMappingContext>> readers) {
      this.readers = readers;
   }

   public ResourceGroupingStrategy getControllerNamingStrategy() {
      return controllerNamingStrategy;
   }

   public void setControllerNamingStrategy(ResourceGroupingStrategy controllerNamingStrategy) {
      this.controllerNamingStrategy = controllerNamingStrategy;
   }

   public AuthorizationContext getAuthorizationContext() {
      return authorizationContext;
   }

   public void setAuthorizationContext(AuthorizationContext authorizationContext) {
      this.authorizationContext = authorizationContext;
   }
}
