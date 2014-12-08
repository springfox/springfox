package com.mangofactory.swagger.readers;

import com.google.common.base.Splitter;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static org.springframework.util.StringUtils.*;

public class MediaTypeReader implements RequestMappingReader {

  @Override
  public void execute(RequestMappingContext context) {
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");

    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
    ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();

    Set<MediaType> consumesMediaTypes = consumesCondition.getConsumableMediaTypes();
    Set<MediaType> producesMediaTypes = producesRequestCondition.getProducibleMediaTypes();

    List<String> consumesList = toList(consumesMediaTypes);
    List<String> producesList = toList(producesMediaTypes);

    ApiOperation annotation = context.getApiOperationAnnotation();
    if (null != annotation && hasText(annotation.consumes())) {
      consumesList = asList(annotation.consumes());
    }

    if (handlerMethodHasFileParameter(context, swaggerGlobalSettings)) {
      //Swagger spec requires consumes is multipart/form-data for file parameter types
      consumesList = Arrays.asList("multipart/form-data");
    }


    if (null != annotation && hasText(annotation.produces())) {
      producesList = asList(annotation.produces());
    }

    //TODO asList() returns unmodifiable collection so any add..() so this add..() can potentially explode,
    // seems wrong to depend on varying type conversions and logic - either immutable or not
    if (producesList.isEmpty()) {
      producesList.add(MediaType.ALL_VALUE);
    }
    if (consumesList.isEmpty()) {
      consumesList.add(MediaType.APPLICATION_JSON_VALUE);
    }
    context.put("consumes", consumesList);
    context.put("produces", producesList);
  }

  private boolean handlerMethodHasFileParameter(RequestMappingContext context,
      SwaggerGlobalSettings swaggerGlobalSettings) {

    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(swaggerGlobalSettings.getTypeResolver());
    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(context.getHandlerMethod());

    for (ResolvedMethodParameter resolvedMethodParameter : methodParameters) {
      if (MultipartFile.class.isAssignableFrom(resolvedMethodParameter.getResolvedParameterType().getErasedType())) {
        return true;
      }
    }
    return false;
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
