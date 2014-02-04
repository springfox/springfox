package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.core.util.ModelUtil;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;

import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.ScalaUtils.*;

public class ApiModelReader implements Command<RequestMappingContext> {
   private static final Logger log = LoggerFactory.getLogger(ApiModelReader.class);

   @Override
   public void execute(RequestMappingContext context) {
      List<ApiDescription> apiDescriptionList = (List<ApiDescription>) context.get("apiDescriptionList");

      log.debug("Reading models for the following apis.. ");
      for(ApiDescription apiDescription : apiDescriptionList){
         log.debug("ApiDescription path:{} ", apiDescription.path());
      }

      //todo probably don't need to call this so many times
      Option<scala.collection.immutable.Map<String, Model>> modelOptions =
              ModelUtil.modelsFromApis(toScalaList(apiDescriptionList));
      Map<String, Model> modelMap = null;

      if (modelOptions != null && null != fromOption(modelOptions)) {
         scala.collection.immutable.Map<String, Model> stringModelMap = fromOption(modelOptions);
         log.debug("Swagger core found {} models", stringModelMap.size());
         modelMap = fromScalaMap(stringModelMap);
      } else{
         log.debug("Swagger core did not find any models");
      }
      context.put("models", modelMap);
   }
}
