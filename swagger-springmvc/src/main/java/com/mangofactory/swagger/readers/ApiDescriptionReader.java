package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Operation;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiDescriptionReader implements Command<RequestMappingContext> {

  private final SwaggerPathProvider swaggerPathProvider;
  private Collection<RequestMappingReader> customAnnotationReaders;

  public ApiDescriptionReader(SwaggerPathProvider pathProvider,
                              Collection<RequestMappingReader> customAnnotationReaders) {
    this.swaggerPathProvider = pathProvider;
    this.customAnnotationReaders = customAnnotationReaders;
  }

  @Override
  public void execute(RequestMappingContext context) {
    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    HandlerMethod handlerMethod = context.getHandlerMethod();
    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();

    List<ApiDescription> apiDescriptionList = newArrayList();
    for (String pattern : patternsCondition.getPatterns()) {
      String cleanedRequestMappingPath = sanitizeRequestMappingPattern(pattern);
      String path = swaggerPathProvider.getOperationPath(cleanedRequestMappingPath);
      String methodName = handlerMethod.getMethod().getName();
      context.put("requestMappingPattern", cleanedRequestMappingPath);
      ApiOperationReader apiOperationReader = new ApiOperationReader(customAnnotationReaders);
      apiOperationReader.execute(context);
      List<Operation> operations = (List<Operation>) context.get("operations");
      apiDescriptionList.add(new ApiDescription(path, toOption(methodName), toScalaList(operations)));
    }
    context.put("apiDescriptionList", apiDescriptionList);
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
  public String sanitizeRequestMappingPattern(String requestMappingPattern) {
    String result = requestMappingPattern;
    //remove regex portion '/{businessId:\\w+}'
    result = result.replaceAll("\\{(.*?):.*?\\}", "{$1}");
    return result.isEmpty() ? "/" : result;
  }
}
