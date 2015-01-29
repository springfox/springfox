package com.mangofactory.documentation.spring.web.scanners;

import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.builder.ApiDescriptionBuilder;
import com.mangofactory.documentation.service.PathProvider;
import com.mangofactory.documentation.service.RequestMappingEvaluator;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import com.mangofactory.documentation.spring.web.readers.operation.ApiOperationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

import static com.google.common.collect.Lists.*;

@Component
public class ApiDescriptionReader {

  private final ApiOperationReader operationReader;

  @Autowired
  public ApiDescriptionReader(ApiOperationReader operationReader) {
    this.operationReader = operationReader;
  }

  public List<ApiDescription> read(RequestMappingContext outerContext) {
    RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
    HandlerMethod handlerMethod = outerContext.getHandlerMethod();
    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
    RequestMappingEvaluator requestMappingEvaluator = outerContext.getDocumentationContext().getRequestMappingEvaluator();
    PathProvider pathProvider = outerContext.getDocumentationContext().getPathProvider();

    List<ApiDescription> apiDescriptionList = newArrayList();
    for (String pattern : patternsCondition.getPatterns()) {
      if (requestMappingEvaluator.shouldIncludePath(pattern)) {
        String cleanedRequestMappingPath = sanitizeRequestMappingPattern(pattern);
        String path = pathProvider.getOperationPath(cleanedRequestMappingPath);
        String methodName = handlerMethod.getMethod().getName();
        RequestMappingContext operationContext = outerContext.copyPatternUsing(cleanedRequestMappingPath);
        apiDescriptionList.add(
                new ApiDescriptionBuilder(outerContext.operationOrdering())
                        .path(path)
                        .description(methodName)
                        .operations(operationReader.read(operationContext))
                        .hidden(false)
                        .build());
      }
    }
    return apiDescriptionList;
  }

  /**
   * Gets a uri friendly path from a request mapping pattern.
   * Typically involves removing any regex patterns or || conditions from a spring request mapping
   * This method will be called to resolve every request mapping endpoint.
   * A good extension point if you need to alter endpoints by adding or removing path segments.
   * Note: this should not be an absolute  uri
   *
   * @param requestMappingPattern
   * @return the request mapping endpoint
   */
  //TODO: Move this to a shared library
  public static String sanitizeRequestMappingPattern(String requestMappingPattern) {
    String result = requestMappingPattern;
    //remove regex portion '/{businessId:\\w+}'
    result = result.replaceAll("\\{([^}]*?):.*?\\}", "{$1}");
    return result.isEmpty() ? "/" : result;
  }
}
