package com.mangofactory.swagger.readers;

import com.google.common.base.Splitter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isBlank;

public class MediaTypeReader implements Command<RequestMappingContext> {

   @Override
   public void execute(RequestMappingContext context) {
      RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
      ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
      ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();

      Set<MediaType> consumesMediaTypes = consumesCondition.getConsumableMediaTypes();
      Set<MediaType> producesMediaTypes = producesRequestCondition.getProducibleMediaTypes();

      List<String> consumesList = toList(consumesMediaTypes);
      List<String> producesList = toList(producesMediaTypes);

      ApiOperation annotation = context.getApiOperationAnnotation();
      if(null != annotation && !isBlank(annotation.consumes())){
         consumesList = asList(annotation.consumes());
      }

      if(null != annotation && !isBlank(annotation.produces())){
         producesList = asList(annotation.produces());
      }

      context.put("consumes", consumesList);
      context.put("produces", producesList);
   }

   private List<String> asList(String mediaTypes) {
      return Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .splitToList(mediaTypes);
   }

   private List<String> toList(Set<MediaType> mediaTypeSet) {
      List<String> mediaTypes = newArrayList();
      for (MediaType mediaType : mediaTypeSet) {
         mediaTypes.add(mediaType.toString());
      }
      return mediaTypes;
   }
}
