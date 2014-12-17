package com.mangofactory.swagger.readers;

import com.google.common.collect.Lists;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.models.dto.Authorization;
import com.mangofactory.swagger.models.dto.Operation;
import com.mangofactory.swagger.models.dto.Parameter;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import com.mangofactory.swagger.ordering.OperationPositionalOrdering;
import com.mangofactory.swagger.readers.operation.DefaultResponseMessageReader;
import com.mangofactory.swagger.readers.operation.OperationAuthReader;
import com.mangofactory.swagger.readers.operation.OperationDeprecatedReader;
import com.mangofactory.swagger.readers.operation.OperationHiddenReader;
import com.mangofactory.swagger.readers.operation.OperationHttpMethodReader;
import com.mangofactory.swagger.readers.operation.OperationImplicitParameterReader;
import com.mangofactory.swagger.readers.operation.OperationImplicitParametersReader;
import com.mangofactory.swagger.readers.operation.OperationNicknameReader;
import com.mangofactory.swagger.readers.operation.OperationNotesReader;
import com.mangofactory.swagger.readers.operation.OperationPositionReader;
import com.mangofactory.swagger.readers.operation.OperationResponseClassReader;
import com.mangofactory.swagger.readers.operation.OperationSummaryReader;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.readers.operation.parameter.OperationParameterReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

public class ApiOperationReader implements Command<RequestMappingContext> {

  private static final Set<RequestMethod> allRequestMethods = new LinkedHashSet<RequestMethod>(Arrays.asList(RequestMethod
          .values()));

  public static final OperationPositionalOrdering OPERATION_POSITIONAL_ORDERING = new OperationPositionalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;

  public ApiOperationReader(Collection<RequestMappingReader> customAnnotationReaders) {
    this.customAnnotationReaders = customAnnotationReaders == null
            ? Lists.<RequestMappingReader>newArrayList()
            : customAnnotationReaders;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(RequestMappingContext outerContext) {

    RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
    HandlerMethod handlerMethod = outerContext.getHandlerMethod();
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) outerContext.get("swaggerGlobalSettings");
    AuthorizationContext authorizationContext = (AuthorizationContext) outerContext.get("authorizationContext");
    String requestMappingPattern = (String) outerContext.get("requestMappingPattern");
    RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
    Set<ResponseMessage> responseMessages = newHashSet();
    List<Operation> operations = newArrayList();

    Set<RequestMethod> requestMethods = requestMethodsRequestCondition.getMethods();
    Set<RequestMethod> supportedMethods = (requestMethods == null || requestMethods.isEmpty())
            ? allRequestMethods
            : requestMethods;

    List<RequestMappingReader> commandList = newArrayList();
    commandList.add(new OperationHiddenReader());
    commandList.add(new OperationAuthReader());
    commandList.add(new OperationHttpMethodReader());
    commandList.add(new OperationSummaryReader());
    commandList.add(new OperationNotesReader());
    commandList.add(new OperationResponseClassReader());
    commandList.add(new OperationNicknameReader());
    commandList.add(new OperationPositionReader());
    commandList.add(new OperationParameterReader());
    commandList.add(new OperationImplicitParametersReader());
    commandList.add(new OperationImplicitParameterReader());
    commandList.add(new OperationParameterRequestConditionReader());
    commandList.add(new MediaTypeReader());
    commandList.add(new DefaultResponseMessageReader());
    commandList.add(new OperationDeprecatedReader());
    commandList.addAll(customAnnotationReaders);

    //Setup response message list

    Integer currentCount = 0;
    Boolean isHidden;
    for (RequestMethod httpRequestMethod : supportedMethods) {
      CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();

      RequestMappingContext operationRequestMappingContext = new RequestMappingContext(requestMappingInfo,
              handlerMethod);
      operationRequestMappingContext.put("currentCount", currentCount);
      operationRequestMappingContext.put("currentHttpMethod", httpRequestMethod);
      operationRequestMappingContext.put("swaggerGlobalSettings", swaggerGlobalSettings);
      operationRequestMappingContext.put("authorizationContext", authorizationContext);
      operationRequestMappingContext.put("requestMappingPattern", requestMappingPattern);
      operationRequestMappingContext.put("responseMessages", responseMessages);

      Map<String, Object> operationResultMap = commandExecutor.execute(commandList, operationRequestMappingContext);
      currentCount = (Integer) operationResultMap.get("currentCount");

      List<String> producesMediaTypes = (List<String>) operationResultMap.get("produces");
      List<String> consumesMediaTypes = (List<String>) operationResultMap.get("consumes");
      List<Parameter> parameterList = (List<Parameter>) operationResultMap.get("parameters");
      List<Authorization> authorizations = (List<Authorization>) operationResultMap.get("authorizations");
      isHidden = (Boolean) operationResultMap.get("isHidden");

      if (!isHidden) {
        Operation operation = new Operation(
                (String) operationResultMap.get("httpRequestMethod"),
                (String) operationResultMap.get("summary"),
                (String) operationResultMap.get("notes"),
                (String) operationResultMap.get("responseClass"),
                (String) operationResultMap.get("nickname"),
                (Integer) operationResultMap.get("position"),
                producesMediaTypes,
                consumesMediaTypes,
                new ArrayList<String>(0),
                authorizations,
                parameterList,
                (Set) operationResultMap.get("responseMessages"),
                (String) operationResultMap.get("deprecated")
        );

        operations.add(operation);
      }
    }
    Collections.sort(operations, OPERATION_POSITIONAL_ORDERING);
    outerContext.put("operations", operations);
  }

}
