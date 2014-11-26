package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.Collection;
import java.util.List;

/**
 * Deprecated as of 0.9.2. This class violates SO"L"ID; explained in #427
 */
@Deprecated
public abstract class SwaggerResponseMessageReader implements RequestMappingReader {

  @Override
  public void execute(RequestMappingContext context) {
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
    HandlerMethod handlerMethod = context.getHandlerMethod();

    //Re-instates this to address #427 :(, Hack will go away as part of removing deprecated class
    @SuppressWarnings("unchecked")
    List<ResponseMessage> responseMessages = (List<ResponseMessage>) context.get("responseMessages");
    responseMessages.addAll(read(swaggerGlobalSettings, currentHttpMethod, handlerMethod));
    context.put("responseMessages", responseMessages);
  }

  protected abstract Collection<ResponseMessage> read(SwaggerGlobalSettings swaggerGlobalSettings,
      RequestMethod currentHttpMethod, HandlerMethod handlerMethod);
}
