package com.mangofactory.swagger.readers.operation;

import com.google.common.collect.Lists;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.Collection;

/**
 * @author fgaule
 * @since 22/07/2014
 */
public abstract class SwaggerResponseMessageReader implements RequestMappingReader {

  @Override
  public void execute(RequestMappingContext context) {
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
    HandlerMethod handlerMethod = context.getHandlerMethod();

    Object responseMessages1 = context.get("responseMessages");
    if (responseMessages1 == null){
      responseMessages1 = Lists.newArrayList();
    }

    Collection<ResponseMessage> responseMessages = (Collection<ResponseMessage>) responseMessages1;
    responseMessages.addAll(read(swaggerGlobalSettings, currentHttpMethod, handlerMethod));
    context.put("responseMessages", responseMessages);
  }

  protected abstract Collection<ResponseMessage> read(SwaggerGlobalSettings swaggerGlobalSettings,
                                                      RequestMethod currentHttpMethod, HandlerMethod handlerMethod);
}
