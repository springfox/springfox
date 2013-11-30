package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;

public class ParameterAllowableReader implements Command<RequestMappingContext> {


   @Override
   public void execute(RequestMappingContext context) {

//      AllowableListValues
//            AnyAllowableValues
//      AllowableRangeValues
//      (AllowableListValues)context.get("allowableValues"),
//            (String) result.get("paramType"),
//            toOption(result.get("paramAccess"))
   }
}
