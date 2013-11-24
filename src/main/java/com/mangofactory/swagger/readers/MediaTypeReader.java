package com.mangofactory.swagger.readers;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class MediaTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
      ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
      ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();

      Set<MediaType> consumesMediaTypes = consumesCondition.getConsumableMediaTypes();
      Set<MediaType> producesMediaTypes = producesRequestCondition.getProducibleMediaTypes();

      context.put("consumes",  toList(consumesMediaTypes));
      context.put("produces",  toList(producesMediaTypes));
   }

   private List<String> toList(Set<MediaType> mediaTypeSet) {
      List<String> mediaTypes = newArrayList();
      for (MediaType mediaType : mediaTypeSet) {
         mediaTypes.add(mediaType.toString());
      }
      return mediaTypes;
   }
}
