package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.readers.MediaTypeReader;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiListing;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class ApiListingScanner {

   private Map<String, List<RequestMappingContext>>  resourceGroupRequestMappings;
   private String apiVersion = "1.0";
   private String swaggerVersion = SwaggerSpec.version();
   private String basePath = "";

   @Getter
   @Setter
   private List<Command> readers = newArrayList();

   public ApiListingScanner(Map<String, List<RequestMappingContext>> resourceGroupRequestMappings) {
      this.resourceGroupRequestMappings = resourceGroupRequestMappings;

   }

   public List<ApiListing> scan() {


      scala.collection.immutable.List emptyList = toScalaList(null);
      List<ApiListing> apiListings = newArrayList();

      int position = 0;
      for (Map.Entry<String, List<RequestMappingContext>> entry : resourceGroupRequestMappings.entrySet()) {
         String resourcePath = entry.getKey();

         Set<String> produces = new LinkedHashSet<String>(2);
         Set<String> consumes = new LinkedHashSet<String>(2);
         for(RequestMappingContext requestMappingContext : entry.getValue()){
            //add to produces
            //add to consumes


            CommandExecutor<Map<String, Object>> commandExecutor = new CommandExecutor();
            readers.add(new MediaTypeReader());

            Map<String, Object> results = commandExecutor.execute(readers, requestMappingContext);

            List<String> producesMediaTypes = (List<String>) results.get("produces");
            List<String> consumesMediaTypes = (List<String>) results.get("consumes");
            produces.addAll(producesMediaTypes);
            consumes.addAll(consumesMediaTypes);


//            requestMappingInfo.
//            ApiBuilder apiBuilder = new ApiBuilder()
//                  .with
//
//add to protocols
            //add to authorizations
            //add to
            //Add to models
            //set description
            //set position
         }


             /*
      apiVersion: String,
  swaggerVersion: String,
  basePath: String,
  resourcePath: String,
  produces: List[String] = List.empty,
  consumes: List[String] = List.empty,
  protocols: List[String] = List.empty,
  authorizations: List[String] = List.empty,
  apis: List[ApiDescription] = List(),
  models: Option[Map[String, Model]] = None,
  description: Option[String] = None,
  position: Int = 0)

       */
         ApiListing apiListing = new ApiListing(
               apiVersion,
               swaggerVersion,
               basePath,
               resourcePath,
               toScalaList(produces),
               toScalaList(consumes),
               emptyList, emptyList, emptyList, toOption(null), toOption(null), position++);
         apiListings.add(apiListing);
      }
      return apiListings;
   }



}
