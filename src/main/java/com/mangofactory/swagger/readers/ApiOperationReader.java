package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.readers.operation.*;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiOperationReader implements Command<RequestMappingContext> {

   private static final Set<RequestMethod> allRequestMethods
         = new HashSet<RequestMethod>(Arrays.asList(RequestMethod.values()));

   @Override
   public void execute(RequestMappingContext outerContext) {

      RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
      HandlerMethod handlerMethod = outerContext.getHandlerMethod();
      Set<Class> ignorableParameterTypes = (Set<Class>) outerContext.get("ignorableParameterTypes");
      Map<Class, String> parameterDataTypes = (Map<Class, String>) outerContext.get("parameterDataTypes");

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
         operationRequestMappingContext.put("ignorableParameterTypes", ignorableParameterTypes);
         operationRequestMappingContext.put("parameterDataTypes", parameterDataTypes);

         commandList.add(new OperationHttpMethodReader());
         commandList.add(new OperationSummaryReader());
         commandList.add(new OperationNotesReader());
         commandList.add(new OperationResponseClassReader()); //td
         commandList.add(new OperationNicknameReader());
         commandList.add(new OperationPositionReader());
         commandList.add(new MediaTypeReader());
         commandList.add(new OperationParameterReader());
         commandList.add(new OperationResponseMessageReader()); //td
         commandList.add(new OperationDeprecatedReader());
         commandExecutor.execute(commandList, operationRequestMappingContext);


         Map<String, Object> operationResultMap = operationRequestMappingContext.getResult();
         currentCount = (Integer) operationResultMap.get("currentCount");

         List<String> producesMediaTypes = (List<String>) operationResultMap.get("produces");
         List<String> consumesMediaTypes = (List<String>) operationResultMap.get("consumes");
         List<Parameter> parameterList = (List<Parameter>) operationResultMap.get("parameters");
         Operation operation = new Operation(
               (String) operationResultMap.get("httpRequestMethod"),
               (String) operationResultMap.get("summary"),
               (String) operationResultMap.get("notes"),
               "responseClass",
               (String) operationResultMap.get("nickname"),
               (Integer) operationResultMap.get("position"),
               toScalaList(producesMediaTypes),
               toScalaList(consumesMediaTypes),
               emptyScalaList(),
               emptyScalaList(),
               toScalaList(parameterList),
               emptyScalaList(),
               toOption(operationResultMap.get("deprecated"))
         );
         operations.add(operation);
      }
      outerContext.put("operations", operations);
   }
}
