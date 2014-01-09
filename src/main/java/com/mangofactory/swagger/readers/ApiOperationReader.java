package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
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
      SwaggerGlobalSettings swaggerGlobalSettings  = (SwaggerGlobalSettings) outerContext.get("swaggerGlobalSettings");

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
               emptyScalaList(),
               toScalaList(parameterList),
               toScalaList((List) operationResultMap.get("responseMessages")),
               toOption(operationResultMap.get("deprecated"))
         );
//         case class Operation (
//               method: String,
//               summary: String,
//               notes: String,
//               responseClass: String,
//               nickname: String,
//               position: Int,
//               produces: List[String] = List.empty,
//               consumes: List[String] = List.empty,
//               protocols: List[String] = List.empty,
//               authorizations: List[String] = List.empty,
//               parameters: List[Parameter] = List.empty,
//               responseMessages: List[ResponseMessage] = List.empty,
//         `deprecated`: Option[String] = None)


         operations.add(operation);
      }
      outerContext.put("operations", operations);
   }
}
