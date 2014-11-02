package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.address.SwaggerAddressProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ApiPathReader implements Command<RequestMappingContext> {

  private final SwaggerAddressProvider swaggerAddressProvider;
  private Collection<RequestMappingReader> customAnnotationReaders;
  private static final Logger log = LoggerFactory.getLogger(ApiPathReader.class);

  public ApiPathReader(SwaggerAddressProvider pathProvider,
                       Collection<RequestMappingReader> customAnnotationReaders) {
    this.swaggerAddressProvider = pathProvider;
    this.customAnnotationReaders = customAnnotationReaders;
  }

  @Override
  public void execute(RequestMappingContext context) {
    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
    Map<String, Path> swaggerPaths = newHashMap();

    for (String pattern : patternsCondition.getPatterns()) {
      String cleanedRequestMappingPath = sanitizeRequestMappingPattern(pattern);
      String url = swaggerAddressProvider.getOperationPath(cleanedRequestMappingPath);
      context.put("requestMappingPattern", cleanedRequestMappingPath);
      ApiOperationReader apiOperationReader = new ApiOperationReader(customAnnotationReaders);
      apiOperationReader.execute(context);

      Map<RequestMethod, Operation> operations = (Map<RequestMethod, Operation>) context.get("operations");
      Path swaggerPath = new Path();

      for (Map.Entry<RequestMethod, Operation> entry : operations.entrySet()) {
        swaggerPath.set(entry.getKey().toString().toLowerCase(), entry.getValue());
      }

      log.debug("Setting path [{}] [{}]", url, swaggerPath);
      swaggerPaths.put(url, swaggerPath);
//      apiDescriptionList.add(new ApiDescription(path, toOption(methodName), toScalaList(operations), false));
    }
    context.put("swaggerPaths", swaggerPaths);
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
