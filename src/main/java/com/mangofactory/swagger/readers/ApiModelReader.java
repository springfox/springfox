package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.core.util.ModelUtil;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Model;
import org.springframework.web.method.HandlerMethod;
import scala.Option;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiModelReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      SwaggerSchemaConverter swaggerSchemaConverter = new SwaggerSchemaConverter();
      List<Model> models = newArrayList();
      ApiOperation methodAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);
      List<ApiDescription> apiDescriptionList = (List<ApiDescription>) context.get("apiDescriptionList");

      //todo probably don't need to call this so many times
      Option<scala.collection.immutable.Map<String, Model>> modelOptions =
              ModelUtil.modelsFromApis(toScalaList(apiDescriptionList));

      if (modelOptions != null && null != fromOption(modelOptions)) {
         scala.collection.immutable.Map<String, Model> stringModelMap = fromOption(modelOptions);
         Map<String, Model> modelMap = fromScalaMap(stringModelMap);
         models.addAll(modelMap.values());
      }

      List<Model> results = newArrayList();
      for (Model m : models) {
         results.add(decorate(m));
      }
      context.put("models", results);
   }

   private Model decorate(Model model) {
      if (null == model) {
         return model;
      }
      Model decorated = new Model(model.qualifiedType(),
              model.qualifiedType(),
              model.qualifiedType(),
              model.properties(),
              model.description(),
              model.baseModel(),
              model.discriminator(),
              model.subTypes());
      return decorated;
   }
}
