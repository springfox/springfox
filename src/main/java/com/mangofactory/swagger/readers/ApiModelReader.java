package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import scala.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.mangofactory.swagger.ScalaUtils.fromOption;

public class ApiModelReader implements Command<RequestMappingContext> {
   private static final Logger log = LoggerFactory.getLogger(ApiModelReader.class);
   private SwaggerSchemaConverter parser = new SwaggerSchemaConverter();

   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();

      log.debug("Reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());

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
      modelMap.putAll( readParametersApiModel(handlerMethod));

      log.debug("Finished reading models for handlerMethod |{}|", handlerMethod.getMethod().getName());
      context.put("models", modelMap);
   }

   private Map<String, Model> readParametersApiModel(HandlerMethod handlerMethod ){

       Method method = handlerMethod.getMethod();
       Map<String, Model> modelMap = newHashMap();

       log.debug("Reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());

       Class<?>[] parameterTypes = method.getParameterTypes();
       Annotation[][] annotations = method.getParameterAnnotations();

       for (int i=0; i < annotations.length; i++){
           Annotation[] pAnnotations = annotations[i];
           for (Annotation annotation : pAnnotations){
               if (annotation instanceof RequestBody){
                   Class<?> pType = parameterTypes[i];
                   String pSchemaName = pType.isArray() ? pType.getComponentType().getSimpleName() : pType.getSimpleName();
                   Option<Model> spModel = parser.read(pType, new scala.collection.immutable.HashMap());
                   Model pModel = fromOption(spModel);
                   if (null != pModel) {
                       log.debug("Swagger generated parameter model {} models", pModel.id());
                       modelMap.put(pSchemaName, pModel);
                   } else {
                       log.debug("Swagger core did not find any parameter models for {}", pSchemaName);
                   }
               }
           }
       }
       log.debug("Finished reading parameters models for handlerMethod |{}|", handlerMethod.getMethod().getName());
       return modelMap;
   }
}
