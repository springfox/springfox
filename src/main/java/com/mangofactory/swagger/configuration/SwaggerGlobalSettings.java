package com.mangofactory.swagger.configuration;

import com.wordnik.swagger.model.ResponseMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwaggerGlobalSettings {

   @Setter
   @Getter
   /**
    * Set of classes to exclude from spring controller request mapping methods
    * e.g HttpServletRequest, BindingResult
    */
   private Set<Class> ignorableParameterTypes;

   @Setter
   @Getter
   /**
    * Map of java primitive types to swagger specific parameter dataTypes
    */
   private Map<Class, String> parameterDataTypes;

   @Setter
   @Getter
   /**
    * Map of spring RequestMethod's to a list of http status codes and accompanying messages
    * @see com.mangofactory.swagger.readers.operation.OperationResponseMessageReader
    */
   private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
}
