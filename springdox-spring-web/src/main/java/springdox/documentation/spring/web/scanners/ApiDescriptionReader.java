package springdox.documentation.spring.web.scanners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springdox.documentation.PathProvider;
import springdox.documentation.RequestMappingEvaluator;
import springdox.documentation.builders.ApiDescriptionBuilder;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.spi.service.contexts.RequestMappingContext;
import springdox.documentation.spring.web.Paths;
import springdox.documentation.spring.web.readers.operation.ApiOperationReader;

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
        String cleanedRequestMappingPath = Paths.sanitizeRequestMappingPattern(pattern);
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

}
