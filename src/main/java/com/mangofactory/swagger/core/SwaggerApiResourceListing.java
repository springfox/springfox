package com.mangofactory.swagger.core;

import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.ResourceListing;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

import static com.mangofactory.swagger.ScalaUtils.toList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class SwaggerApiResourceListing {
   @Getter
   private ResourceListing resourceListing;
   @Getter
   @Setter
   private ApiInfo apiInfo;
   @Getter
   @Setter
   private List<AuthorizationType> authorizationTypes;
   @Getter
   @Setter
   private String resourceListingPath = "/api-docs";

   private ServletContext servletContext;

   public SwaggerApiResourceListing(ServletContext servletContext) {
      this.servletContext = servletContext;
   }

   public void createResourceListing() {
      this.resourceListing = new ResourceListing(
          "1", SwaggerSpec.version(),
          toList(new ArrayList<ApiListingReference>()),
          toList(authorizationTypes), toOption(apiInfo));

   }


}
