package springdox.documentation;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RequestHandler {
  private final RequestMappingInfo requestMapping;
  private final HandlerMethod handlerMethod;

  public RequestHandler(RequestMappingInfo requestMapping, HandlerMethod handlerMethod) {
    this.requestMapping = requestMapping;
    this.handlerMethod = handlerMethod;
  }

  public RequestMappingInfo getRequestMapping() {
    return requestMapping;
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }
}
