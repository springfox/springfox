package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.address.SwaggerAddressProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.models.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class RequestMappingOperationReader implements Command<RequestMappingContext> {

  private final SwaggerAddressProvider swaggerAddressProvider;
  private Collection<RequestMappingReader> customAnnotationReaders;
  private static final Logger log = LoggerFactory.getLogger(RequestMappingOperationReader.class);

  public RequestMappingOperationReader(SwaggerAddressProvider pathProvider,
                                       Collection<RequestMappingReader> customAnnotationReaders) {
    this.swaggerAddressProvider = pathProvider;
    this.customAnnotationReaders = customAnnotationReaders;
  }

  @Override
  public void execute(RequestMappingContext context) {
    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
    Map<String, Map<RequestMethod, Operation>> requestMappingOperations = newHashMap();

    for (String pattern : patternsCondition.getPatterns()) {
      String cleanedRequestMappingPath = sanitizeRequestMappingPattern(pattern);
      String url = swaggerAddressProvider.getOperationPath(cleanedRequestMappingPath);
      context.put("requestMappingPattern", cleanedRequestMappingPath);
      ApiOperationReader apiOperationReader = new ApiOperationReader(customAnnotationReaders);
      apiOperationReader.execute(context);

      Map<RequestMethod, Operation> operations = (Map<RequestMethod, Operation>) context.get("operations");
      log.debug("Setting operations for path: [{}] operation: [{}]", url, operations);
      requestMappingOperations.put(url, operations);
    }
    context.put("requestMappingOperations", requestMappingOperations);
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
    result = result.replaceAll("\\{([^}]*?):.*?\\}", "{$1}");
    return result.isEmpty() ? "/" : result;
  }
}
