package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.core.SwaggerPathProvider;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Operation;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;

public class ApiDescriptionReader implements Command<RequestMappingContext> {

   private final SwaggerPathProvider swaggerPathProvider;
   private ResourceGroupingStrategy resourceGroupingStrategy;

   public ApiDescriptionReader(SwaggerPathProvider pathProvider,
                               ResourceGroupingStrategy namingStrategy) {

      this.resourceGroupingStrategy = namingStrategy;
      this.swaggerPathProvider = pathProvider;
   }

   @Override
   public void execute(RequestMappingContext context) {

      RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
      HandlerMethod handlerMethod = context.getHandlerMethod();
      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();

      List<ApiDescription> apiDescriptionList = newArrayList();
      for (String pattern : patternsCondition.getPatterns()) {

         String cleanedRequestMappingPath = swaggerPathProvider.getRequestMappingEndpoint(pattern);
         String path = swaggerPathProvider.getApiResourcePrefix()
                 + cleanedRequestMappingPath;

         String methodName = handlerMethod.getMethod().getName();

         context.put("requestMappingPattern", cleanedRequestMappingPath);
         ApiOperationReader apiOperationReader = new ApiOperationReader();
         apiOperationReader.execute(context);
         List<Operation> operations = (List<Operation>) context.get("operations");

         apiDescriptionList.add(new ApiDescription(path, toOption(methodName), toScalaList(operations)));
      }
      context.put("apiDescriptionList", apiDescriptionList);
   }
}
