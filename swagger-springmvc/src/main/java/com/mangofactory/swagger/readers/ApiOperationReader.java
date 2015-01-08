package com.mangofactory.swagger.readers;

import com.mangofactory.service.model.Operation;
import com.mangofactory.service.model.builder.OperationBuilder;
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager;
import com.mangofactory.springmvc.plugins.OperationContext;
import com.mangofactory.swagger.ordering.OperationPositionalOrdering;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;

@Component
public class ApiOperationReader implements Command<RequestMappingContext> {

  private static final Set<RequestMethod> allRequestMethods  = new LinkedHashSet<RequestMethod>(asList(RequestMethod.values()));
  private static final OperationPositionalOrdering OPERATION_POSITIONAL_ORDERING = new OperationPositionalOrdering();
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiOperationReader(DocumentationPluginsManager pluginsManager) {
    this.pluginsManager = pluginsManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(RequestMappingContext outerContext) {

    RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
    String requestMappingPattern = outerContext.getRequestMappingPattern();
    RequestMethodsRequestCondition requestMethodsRequestCondition = requestMappingInfo.getMethodsCondition();
    List<Operation> operations = newArrayList();

    Set<RequestMethod> requestMethods = requestMethodsRequestCondition.getMethods();
    Set<RequestMethod> supportedMethods = requestMethods == null || requestMethods.isEmpty()
            ? allRequestMethods
            : requestMethods;

    //Setup response message list
    Integer currentCount = 0;
    for (RequestMethod httpRequestMethod : supportedMethods) {
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              httpRequestMethod,
              outerContext.getHandlerMethod(),
              currentCount,
              requestMappingInfo,
              outerContext.getDocumentationContext(), requestMappingPattern);

      Operation operation = pluginsManager.operation(operationContext);
      if (!operation.isHidden()) {
        operations.add(operation);
        currentCount++;
      }
    }
    Collections.sort(operations, OPERATION_POSITIONAL_ORDERING);
    outerContext.put("operations", operations);
  }

}
