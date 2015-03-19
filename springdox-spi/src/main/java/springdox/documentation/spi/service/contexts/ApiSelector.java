package springdox.documentation.spi.service.contexts;

import com.google.common.base.Predicate;
import springdox.documentation.RequestHandler;
import springdox.documentation.annotations.ApiIgnore;
import springdox.documentation.builders.PathSelectors;
import springdox.documentation.builders.RequestHandlerSelectors;

import static com.google.common.base.Predicates.not;

public class ApiSelector {
  public static final ApiSelector DEFAULT
      = new ApiSelector(not(RequestHandlerSelectors.withClassAnnotation(ApiIgnore.class)), PathSelectors.any());
  private final Predicate<RequestHandler> requestHandlerSelector;
  private final Predicate<String> pathSelector;

  public ApiSelector(Predicate<RequestHandler> requestHandlerSelector, Predicate<String> pathSelector) {
    this.requestHandlerSelector = requestHandlerSelector;
    this.pathSelector = pathSelector;
  }

  public Predicate<RequestHandler> getRequestHandlerSelector() {
    return requestHandlerSelector;
  }

  public Predicate<String> getPathSelector() {
    return pathSelector;
  }
}
