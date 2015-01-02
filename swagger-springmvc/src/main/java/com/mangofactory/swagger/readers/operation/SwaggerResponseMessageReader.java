package com.mangofactory.swagger.readers.operation;

import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Deprecated as of 0.9.2. This class violates SO"L"ID; explained in #427
 */
@Deprecated
public abstract class SwaggerResponseMessageReader implements RequestMappingReader {

  @Override
  public void execute(RequestMappingContext context) {
    RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
    HandlerMethod handlerMethod = context.getHandlerMethod();

    //Re-instates this to address #427 :(, Hack will go away as part of removing deprecated class
    @SuppressWarnings("unchecked")
    Set<ResponseMessage> responseMessages = (Set<ResponseMessage>) context.get("responseMessages");
    Collection<ResponseMessage> read = read(context, currentHttpMethod, handlerMethod);
    context.put("responseMessages", newSet(responseMessages, read));
  }

  private Set<ResponseMessage> newSet(Set<ResponseMessage> responseMessages, Collection<ResponseMessage> read) {
    TreeSet<ResponseMessage> toSet = new TreeSet<ResponseMessage>(new Comparator<ResponseMessage>() {
      @Override
      public int compare(ResponseMessage first, ResponseMessage second) {
        return first.getCode() - second.getCode();
      }
    });
    toSet.addAll(responseMessages);
    toSet.addAll(read);
    return toSet;
  }

  protected abstract Collection<ResponseMessage> read(RequestMappingContext context,
                                                      RequestMethod currentHttpMethod,
                                                      HandlerMethod handlerMethod);
}
