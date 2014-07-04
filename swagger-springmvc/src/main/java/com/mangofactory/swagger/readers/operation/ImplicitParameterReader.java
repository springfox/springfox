package com.mangofactory.swagger.readers.operation;

import com.google.common.base.Splitter;
import com.mangofactory.swagger.readers.Command;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableRangeValues;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.*;
import static org.apache.commons.lang.StringUtils.isBlank;

public class ImplicitParameterReader implements Command<RequestMappingContext> {
   @Override
   public void execute(RequestMappingContext context) {
      HandlerMethod handlerMethod = context.getHandlerMethod();
      Method method = handlerMethod.getMethod();
      if (!method.isAnnotationPresent(ApiImplicitParam.class)) {
         return;
      }
      ApiImplicitParam annotation = method.getAnnotation(ApiImplicitParam.class);
      List<Parameter> parameters = (List<Parameter>) context.get("parameters");
      if (parameters == null) {
         parameters = newArrayList();
      }
      parameters.add(this.getImplicitParameter(annotation));
      context.put("parameters", parameters);
   }

   public Parameter getImplicitParameter(ApiImplicitParam param) {
      AllowableValues allowableValues = null;
      String allowableValueString = param.allowableValues();
      if (allowableValueString!=null && !"".equals(allowableValueString)) {
         allowableValueString = allowableValueString.trim().replaceAll(" ", "");
         if (allowableValueString.startsWith("range[")) {
            allowableValueString = allowableValueString.replaceAll("range\\[", "").replaceAll("]", "");
            Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
            List<String> ranges = newArrayList(split);
            allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
         } else if (allowableValueString.contains(",")) {
            Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
            allowableValues = new AllowableListValues(toScalaList(newArrayList(split)), "LIST");
         } else if (!isBlank(allowableValueString)) {
            List<String> singleVal = Arrays.asList(allowableValueString.trim());
            allowableValues = new AllowableListValues(toScalaList(singleVal), "LIST");
         }
      }
      Parameter parameter = new Parameter(
              param.name(),
              toOption(param.value()),
              toOption(param.defaultValue()),
              param.required(),
              param.allowMultiple(),
              param.dataType(),
              allowableValues,
              param.paramType(),
              toOption(param.access())
      );
      return parameter;
   }

}

