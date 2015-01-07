package com.mangofactory.swagger.readers;

import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.Operation;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.OperationBuilder;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.core.CommandExecutor;
import com.mangofactory.swagger.ordering.OperationPositionalOrdering;
import com.mangofactory.swagger.readers.operation.DefaultResponseMessageReader;
import com.mangofactory.swagger.readers.operation.OperationAuthReader;
import com.mangofactory.swagger.readers.operation.OperationDeprecatedReader;
import com.mangofactory.swagger.plugins.operation.OperationHiddenReader;
import com.mangofactory.swagger.readers.operation.OperationHttpMethodReader;
import com.mangofactory.swagger.plugins.operation.OperationImplicitParameterReader;
import com.mangofactory.swagger.plugins.operation.OperationImplicitParametersReader;
import com.mangofactory.swagger.readers.operation.OperationNicknameReader;
import com.mangofactory.swagger.readers.operation.OperationNotesReader;
import com.mangofactory.swagger.readers.operation.OperationPositionReader;
import com.mangofactory.swagger.readers.operation.OperationResponseClassReader;
import com.mangofactory.swagger.readers.operation.OperationSummaryReader;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.readers.operation.parameter.OperationParameterReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static java.util.Arrays.asList;

@Component
public class ApiOperationReader implements Command<RequestMappingContext> {

  private static final Set<RequestMethod> allRequestMethods  = new LinkedHashSet<RequestMethod>(asList(RequestMethod.values()));
  private static final OperationPositionalOrdering OPERATION_POSITIONAL_ORDERING = new OperationPositionalOrdering();
  private final MediaTypeReader mediaTypeReader;
  private final OperationResponseClassReader operationClassReader;
  private final OperationParameterReader operationParameterReader;
  private final DefaultResponseMessageReader defaultMessageReader;

  @Autowired
  public ApiOperationReader(MediaTypeReader mediaTypeReader,
                            OperationResponseClassReader operationClassReader,
                            OperationParameterReader operationParameterReader,
                            DefaultResponseMessageReader defaultMessageReader) {
    this.mediaTypeReader = mediaTypeReader;
    this.operationClassReader = operationClassReader;
    this.operationParameterReader = operationParameterReader;
    this.defaultMessageReader = defaultMessageReader;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(RequestMappingContext outerContext) {

    RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
    AuthorizationContext authorizationContext = outerContext.getDocumentationContext().getAuthorizationContext();
    String requestMappingPattern = (String) outerContext.get("requestMappingPattern");
    RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
    Set<ResponseMessage> responseMessages = newHashSet();
    List<Operation> operations = newArrayList();

    Set<RequestMethod> requestMethods = requestMethodsRequestCondition.getMethods();
    Set<RequestMethod> supportedMethods = requestMethods == null || requestMethods.isEmpty()
            ? allRequestMethods
            : requestMethods;

    List<RequestMappingReader> commandList = newArrayList();
    commandList.add(new OperationHiddenReader());
    commandList.add(new OperationAuthReader());
    commandList.add(new OperationHttpMethodReader());
    commandList.add(new OperationSummaryReader());
    commandList.add(new OperationNotesReader());
    commandList.add(operationClassReader);
    commandList.add(new OperationNicknameReader());
    commandList.add(new OperationPositionReader());
    commandList.add(operationParameterReader);
    commandList.add(new OperationImplicitParametersReader());
    commandList.add(new OperationImplicitParameterReader());
    commandList.add(new OperationParameterRequestConditionReader());
    commandList.add(mediaTypeReader);
    commandList.add(defaultMessageReader);
    commandList.add(new OperationDeprecatedReader());
//    commandList.addAll(customAnnotationReaders); //TODO:

    //Setup response message list

    Integer currentCount = 0;
    Boolean isHidden;
    for (RequestMethod httpRequestMethod : supportedMethods) {
      CommandExecutor<Map<String, Object>, RequestMappingContext> commandExecutor = new CommandExecutor();

      RequestMappingContext operationRequestMappingContext = outerContext.newCopy();
      operationRequestMappingContext.put("currentCount", currentCount);
      operationRequestMappingContext.put("currentHttpMethod", httpRequestMethod);
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
        Operation operation = new OperationBuilder()
                .method((String) operationResultMap.get("httpRequestMethod"))
                .summary((String) operationResultMap.get("summary"))
                .notes((String) operationResultMap.get("notes"))
                .responseClass((String) operationResultMap.get("responseClass"))
                .nickname((String) operationResultMap.get("nickname"))
                .position((Integer) operationResultMap.get("position"))
                .produces(producesMediaTypes)
                .consumes(consumesMediaTypes)
                .protocol(new ArrayList<String>(0))
                .authorizations(authorizations)
                .parameters(parameterList)
                .responseMessages((Set) operationResultMap.get("responseMessages"))
                .deprecated((String) operationResultMap.get("deprecated"))
                .build();
        operations.add(operation);
      }
    }
    Collections.sort(operations, OPERATION_POSITIONAL_ORDERING);
    outerContext.put("operations", operations);
  }

}
