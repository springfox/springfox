package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.ControllerResourceNamingStrategy;
import com.mangofactory.swagger.core.DefaultControllerResourceNamingStrategy;
import com.mangofactory.swagger.core.SwaggerPathProvider;
import com.mangofactory.swagger.readers.ApiDescriptionReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiListing;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiListingScanner {

   private final String resourceGroup;
   private String apiVersion = "1.0";
   private String swaggerVersion = SwaggerSpec.version();
   private Map<String, List<RequestMappingContext>>  resourceGroupRequestMappings;
   private SwaggerPathProvider swaggerPathProvider;
   private List<Command<RequestMappingContext>> readers = newArrayList();
   private SwaggerGlobalSettings swaggerGlobalSettings;
   private ControllerResourceNamingStrategy controllerNamingStrategy;

   public ApiListingScanner(Map<String, List<RequestMappingContext>> resourceGroupRequestMappings,
         String resourceGroup, SwaggerPathProvider swaggerPathProvider) {
      this.resourceGroupRequestMappings = resourceGroupRequestMappings;
      this.resourceGroup = resourceGroup;
      this.swaggerPathProvider = swaggerPathProvider;
   }

   public Map<String, ApiListing> scan() {

      if(null == controllerNamingStrategy){
         controllerNamingStrategy = new DefaultControllerResourceNamingStrategy();
      }

      Map<String, ApiListing> apiListingMap = newHashMap();

      int position = 0;
      for (Map.Entry<String, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {

         String controllerGroupName = entry.getKey();

         Set<String> produces = new LinkedHashSet<String>(2);
         Set<String> consumes = new LinkedHashSet<String>(2);
         List<ApiDescription> apiDescriptions = newArrayList();
         for(RequestMappingContext requestMappingContext : entry.getValue()){

            CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();
            readers.add(new MediaTypeReader());
            readers.add(new ApiDescriptionReader(controllerNamingStrategy));

            requestMappingContext.put("swaggerGlobalSettings", swaggerGlobalSettings);
            Map<String, Object> results = commandExecutor.execute(readers, requestMappingContext);

            List<String> producesMediaTypes = (List<String>) results.get("produces");
            List<String> consumesMediaTypes = (List<String>) results.get("consumes");

            produces.addAll(producesMediaTypes);
            consumes.addAll(consumesMediaTypes);

            List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");
            apiDescriptions.addAll(apiDescriptionList);
         }

         String resourcePath = UriComponentsBuilder
               .fromPath(swaggerPathProvider.getApiResourcePrefix())
               .pathSegment(controllerGroupName)
               .build()
               .toString();

         ApiListing apiListing = new ApiListing(
               apiVersion,
               swaggerVersion,
               swaggerPathProvider.getAppBasePath(),
               resourcePath,
               toScalaList(produces),
               toScalaList(consumes),
               emptyScalaList(),
               emptyScalaList(),
               toScalaList(apiDescriptions),
               toOption(null),
               toOption(null),
               position++);
         apiListingMap.put(controllerGroupName, apiListing);
      }
      return apiListingMap;
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

   public ControllerResourceNamingStrategy getControllerNamingStrategy() {
      return controllerNamingStrategy;
   }

   public void setControllerNamingStrategy(ControllerResourceNamingStrategy controllerNamingStrategy) {
      this.controllerNamingStrategy = controllerNamingStrategy;
   }


}
