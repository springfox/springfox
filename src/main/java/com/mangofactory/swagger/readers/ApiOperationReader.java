package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.operation.OperationAuthReader;
import com.mangofactory.swagger.readers.operation.OperationDeprecatedReader;
import com.mangofactory.swagger.readers.operation.OperationHttpMethodReader;
import com.mangofactory.swagger.readers.operation.OperationNicknameReader;
import com.mangofactory.swagger.readers.operation.OperationNotesReader;
import com.mangofactory.swagger.readers.operation.OperationParameterReader;
import com.mangofactory.swagger.readers.operation.OperationPositionReader;
import com.mangofactory.swagger.readers.operation.OperationResponseClassReader;
import com.mangofactory.swagger.readers.operation.OperationResponseMessageReader;
import com.mangofactory.swagger.readers.operation.OperationSummaryReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.Authorization;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.emptyScalaList;
import static com.mangofactory.swagger.ScalaUtils.toOption;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;

public class ApiOperationReader implements Command<RequestMappingContext> {

   private static final Set<RequestMethod> allRequestMethods = new HashSet<RequestMethod>(Arrays.asList(RequestMethod.values()));
   public ApiOperationReader() { }

   @Override
   public void execute(RequestMappingContext outerContext) {

      RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
      HandlerMethod handlerMethod = outerContext.getHandlerMethod();
      SwaggerGlobalSettings swaggerGlobalSettings  = (SwaggerGlobalSettings) outerContext.get("swaggerGlobalSettings");
      AuthorizationContext authorizationContext = (AuthorizationContext) outerContext.get("authorizationContext");
      String requestMappingPattern = (String) outerContext.get("requestMappingPattern");
      RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
      List<Operation> operations = newArrayList();

      Set<RequestMethod> requestMethods = requestMethodsRequestCondition.getMethods();
      Set<RequestMethod> supportedMethods = (requestMethods == null || requestMethods.isEmpty())
            ? allRequestMethods : requestMethods;


      Integer currentCount = 0;
      for (RequestMethod httpRequestMethod : supportedMethods) {
         CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();

         List<Command<RequestMappingContext>> commandList = newArrayList();
         RequestMappingContext operationRequestMappingContext = new RequestMappingContext(requestMappingInfo, handlerMethod);
         operationRequestMappingContext.put("currentCount", currentCount);
         operationRequestMappingContext.put("currentHttpMethod", httpRequestMethod);
         operationRequestMappingContext.put("swaggerGlobalSettings", swaggerGlobalSettings);
         operationRequestMappingContext.put("authorizationContext", authorizationContext);
         operationRequestMappingContext.put("requestMappingPattern", requestMappingPattern);


         commandList.add(new OperationAuthReader());
         commandList.add(new OperationHttpMethodReader());
         commandList.add(new OperationSummaryReader());
         commandList.add(new OperationNotesReader());
         commandList.add(new OperationResponseClassReader());
         commandList.add(new OperationNicknameReader());
         commandList.add(new OperationPositionReader());
         commandList.add(new MediaTypeReader());
         commandList.add(new OperationParameterReader());
         commandList.add(new OperationResponseMessageReader());
         commandList.add(new OperationDeprecatedReader());
         commandExecutor.execute(commandList, operationRequestMappingContext);


         Map<String, Object> operationResultMap = operationRequestMappingContext.getResult();
         currentCount = (Integer) operationResultMap.get("currentCount");

         List<String> producesMediaTypes = (List<String>) operationResultMap.get("produces");
         List<String> consumesMediaTypes = (List<String>) operationResultMap.get("consumes");
         List<Parameter> parameterList = (List<Parameter>) operationResultMap.get("parameters");
         List<Authorization> authorizations = (List<Authorization>) operationResultMap.get("authorizations");

         Operation operation = new Operation(
               (String) operationResultMap.get("httpRequestMethod"),
               (String) operationResultMap.get("summary"),
               (String) operationResultMap.get("notes"),
               (String) operationResultMap.get("responseClass"),
               (String) operationResultMap.get("nickname"),
               (Integer) operationResultMap.get("position"),
               toScalaList(producesMediaTypes),
               toScalaList(consumesMediaTypes),
               emptyScalaList(),
               toScalaList(authorizations),
               toScalaList(parameterList),
               toScalaList((List) operationResultMap.get("responseMessages")),
               toOption(operationResultMap.get("deprecated"))
         );

         operations.add(operation);
      }
      outerContext.put("operations", operations);
   }
}
