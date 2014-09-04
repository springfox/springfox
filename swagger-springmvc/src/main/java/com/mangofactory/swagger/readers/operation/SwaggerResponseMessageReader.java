package com.mangofactory.swagger.readers.operation;

import com.google.common.base.Optional;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SwaggerResponseMessageReader implements RequestMappingReader {

  @Override
  @SuppressWarnings("unchecked")
  public void execute(RequestMappingContext context) {
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
    HandlerMethod handlerMethod = context.getHandlerMethod();

    Collection<ResponseMessage> responseMessages = (Collection<ResponseMessage>) Optional.fromNullable(context.get
            ("responseMessages")).or(new ArrayList<ResponseMessage>());

    responseMessages.addAll(read(swaggerGlobalSettings, currentHttpMethod, handlerMethod));
    context.put("responseMessages", responseMessages);
  }

  protected abstract Collection<ResponseMessage> read(SwaggerGlobalSettings swaggerGlobalSettings,
      RequestMethod currentHttpMethod, HandlerMethod handlerMethod);
}
