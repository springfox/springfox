package springdox.documentation.spring.web.plugins;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import springdox.documentation.RequestHandler;
import springdox.documentation.spi.service.contexts.ApiSelector;

import static com.google.common.base.Predicates.*;

public class ApiSelectorBuilder {
  private final Docket parent;
  private Predicate<RequestHandler> requestHandlerSelector = ApiSelector.DEFAULT.getRequestHandlerSelector();
  private Predicate<String> pathSelector = ApiSelector.DEFAULT.getPathSelector();

  public ApiSelectorBuilder(Docket parent) {
    this.parent = parent;
  }

  public ApiSelectorBuilder apis(Predicate<RequestHandler> selector) {
    requestHandlerSelector = and(requestHandlerSelector, selector);
    return this;
  }

  public ApiSelectorBuilder paths(Predicate<String> selector) {
    pathSelector = and(pathSelector, selector);
    return this;
  }

  public Docket build() {
    return parent.selector(new ApiSelector(combine(requestHandlerSelector, pathSelector), pathSelector));
  }

  private Predicate<RequestHandler> combine(Predicate<RequestHandler> requestHandlerSelector,
      Predicate<String> pathSelector) {
    return and(requestHandlerSelector, transform(pathSelector));
  }

  private Predicate<RequestHandler> transform(final Predicate<String> pathSelector) {
    return new Predicate<RequestHandler>() {
      @Override
      public boolean apply(RequestHandler input) {
        return Iterables.any(input.getRequestMapping().getPatternsCondition().getPatterns(), pathSelector);
      }
    };
  }
}
