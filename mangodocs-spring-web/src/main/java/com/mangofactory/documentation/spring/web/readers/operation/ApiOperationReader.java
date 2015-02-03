package com.mangofactory.documentation.spring.web.readers.operation;

import com.mangofactory.documentation.service.model.Operation;
import com.mangofactory.documentation.builders.OperationBuilder;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager;
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
public class ApiOperationReader {

  private static final Set<RequestMethod> allRequestMethods
          = new LinkedHashSet<RequestMethod>(asList(RequestMethod.values()));
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ApiOperationReader(DocumentationPluginsManager pluginsManager) {
    this.pluginsManager = pluginsManager;
  }

  public List<Operation> read(RequestMappingContext outerContext) {

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
    Collections.sort(operations, outerContext.operationOrdering());
    return operations;
  }

}
