package com.mangofactory.swagger.core;

import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.WebApplicationContext;

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

   private WebApplicationContext webApplicationContext;

   public SwaggerApiResourceListing(WebApplicationContext webApplicationContext) {
      this.webApplicationContext = webApplicationContext;
   }

   public void createResourceListing() {
      this.resourceListing = new ResourceListing("1", SwaggerSpec.version(),
                                                 toList(new ArrayList<ApiListingReference>()),
                                                 toList(authorizationTypes), toOption(apiInfo));

      ServletContext servletContext = webApplicationContext.getServletContext();
   }


}
