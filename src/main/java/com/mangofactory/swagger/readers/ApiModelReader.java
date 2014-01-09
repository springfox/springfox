package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.ScalaUtils.fromOption;
import static com.mangofactory.swagger.core.ModelUtils.getHandlerReturnType;

public class ApiModelReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      SwaggerSchemaConverter swaggerSchemaConverter = new SwaggerSchemaConverter();
      Class<?> returnType = getHandlerReturnType(handlerMethod);
      Model model = fromOption(swaggerSchemaConverter.read(returnType));
      context.put("model", model);
   }
}
