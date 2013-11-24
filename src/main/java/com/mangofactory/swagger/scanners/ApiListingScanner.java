package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.core.ControllerResourceGroupingStrategy;
import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy;
import com.mangofactory.swagger.readers.ApiDescriptionReader;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiListing;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class ApiListingScanner {

   private final String resourceGroup;
   private String apiVersion = "1.0";
   private String swaggerVersion = SwaggerSpec.version();
   private String basePath = "";
   private Map<String, List<RequestMappingContext>>  resourceGroupRequestMappings;

   @Getter
   @Setter
   private List<Command> readers = newArrayList();

   @Getter
   @Setter
   private ControllerResourceGroupingStrategy controllerNamingStrategy;

   public ApiListingScanner(Map<String, List<RequestMappingContext>> resourceGroupRequestMappings,
         String resourceGroup) {
      this.resourceGroupRequestMappings = resourceGroupRequestMappings;
      this.resourceGroup = resourceGroup;
   }

   public Map<String, ApiListing> scan() {

      if(null == controllerNamingStrategy){
         controllerNamingStrategy = new DefaultControllerResourceGroupingStrategy();
      }

      Map<String, ApiListing> apiListingMap = newHashMap();
      scala.collection.immutable.List emptyList = toScalaList(null);

      int position = 0;
      for (Map.Entry<String, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {

         String controllerGroupName = entry.getKey();

         Set<String> produces = new LinkedHashSet<String>(2);
         Set<String> consumes = new LinkedHashSet<String>(2);
         List<ApiDescription> apiDescriptions = new ArrayList();
         for(RequestMappingContext requestMappingContext : entry.getValue()){

            CommandExecutor<Map<String, Object>> commandExecutor = new CommandExecutor();
            readers.add(new MediaTypeReader());
            readers.add(new ApiDescriptionReader(controllerNamingStrategy));

            Map<String, Object> results = commandExecutor.execute(readers, requestMappingContext);

            List<String> producesMediaTypes = (List<String>) results.get("produces");
            List<String> consumesMediaTypes = (List<String>) results.get("consumes");

            produces.addAll(producesMediaTypes);
            consumes.addAll(consumesMediaTypes);

            List<ApiDescription> apiDescriptionList = (List<ApiDescription>) results.get("apiDescriptionList");
            apiDescriptions.addAll(apiDescriptionList);

         }

         String resourcePath = String.format("/%s/%s", resourceGroup, controllerGroupName);
         ApiListing apiListing = new ApiListing(
               apiVersion,
               swaggerVersion,
               basePath,
               resourcePath,
               toScalaList(produces),
               toScalaList(consumes),
               emptyList, emptyList, toScalaList(apiDescriptions), toOption(null), toOption(null), position++);

         apiListingMap.put(controllerGroupName, apiListing);
      }
      return apiListingMap;
   }
}
