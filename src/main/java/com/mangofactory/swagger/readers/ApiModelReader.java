package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import scala.Option;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.mangofactory.swagger.ScalaUtils.fromOption;

public class ApiModelReader implements Command<RequestMappingContext> {
   private static final Logger log = LoggerFactory.getLogger(ApiModelReader.class);

   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();

      log.debug("Reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());

      SwaggerSchemaConverter parser = new SwaggerSchemaConverter();
      Map<String, Model> modelMap = newHashMap();
      Class<?> modelType = handlerMethod.getMethod().getReturnType();

      ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
      if(null != apiOperationAnnotation && Void.class != apiOperationAnnotation.response()){
         modelType = apiOperationAnnotation.response();
      }

      String schemaName = modelType.isArray() ? modelType.getComponentType().getSimpleName() : modelType.getSimpleName();
      Option<Model> sModel = parser.read(modelType, new scala.collection.immutable.HashMap());
      Model model = fromOption(sModel);

      if(null != model) {
         log.debug("Swagger generated model {} models", model.id());
         modelMap.put(schemaName, model);
      } else{
         log.debug("Swagger core did not find any models");
      }

       Class<?>[] parameterTypes = handlerMethod.getMethod().getParameterTypes();
       for (Class<?> parameterType : parameterTypes) {
           String parameterSchemaName = parameterType.isArray() ? parameterType.getComponentType().getSimpleName() : parameterType.getSimpleName();
           Option<Model> spModel = parser.read(parameterType, new scala.collection.immutable.HashMap());
           Model pModel = fromOption(spModel);
           if (null != pModel) {
               log.debug("Swagger generated parameter model {} models", pModel.id());
               modelMap.put(parameterSchemaName, pModel);
           } else {
               log.debug("Swagger core did not find any parameter models for {}", parameterSchemaName);
           }
       }

      log.debug("Finished reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());
      context.put("models", modelMap);
   }


}
