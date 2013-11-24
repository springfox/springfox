package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.core.ControllerResourceGroupingStrategy;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ApiDescription;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.emptyScalaList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

public class ApiDescriptionReader implements Command<RequestMappingContext> {

   private ControllerResourceGroupingStrategy controllerResourceGroupingStrategy;

   public ApiDescriptionReader(ControllerResourceGroupingStrategy controllerResourceGroupingStrategy) {
      this.controllerResourceGroupingStrategy = controllerResourceGroupingStrategy;
   }

   @Override
   public void execute(RequestMappingContext context) {

      RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
      HandlerMethod handlerMethod = context.getHandlerMethod();

      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();

      List<ApiDescription> apiDescriptionList = newArrayList();

      //New api operation for each pattern
      for (String pattern : patternsCondition.getPatterns()) {

         //TODO - allow prefix & suffix
         String path = controllerResourceGroupingStrategy.getUriSafeRequestMappingPattern(pattern);
         String methodName = handlerMethod.getMethod().getName();
//      case class ApiDescription (
//            path: String,
//            description: Option[String],
//            operations: List[Operation] = List())


         apiDescriptionList.add(new ApiDescription(path, toOption(methodName), emptyScalaList()));
      }
      context.put("apiDescriptionList", apiDescriptionList);
   }
}
