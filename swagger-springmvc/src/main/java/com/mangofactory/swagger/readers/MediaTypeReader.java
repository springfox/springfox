package com.mangofactory.swagger.readers;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Splitter;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.springmvc.plugins.ApiListingBuilderPlugin;
import com.mangofactory.springmvc.plugins.ApiListingContext;
import com.mangofactory.swagger.readers.operation.HandlerMethodResolver;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
public class MediaTypeReader implements RequestMappingReader, ApiListingBuilderPlugin {

  private final TypeResolver typeResolver;

  @Autowired
  public MediaTypeReader(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void execute(RequestMappingContext context) {

    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
    ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();

    Set<MediaType> consumesMediaTypes = consumesCondition.getConsumableMediaTypes();
    Set<MediaType> producesMediaTypes = producesRequestCondition.getProducibleMediaTypes();

    List<String> consumesList = toList(consumesMediaTypes);
    List<String> producesList = toList(producesMediaTypes);

    if (handlerMethodHasFileParameter(context)) {
      //Swagger spec requires consumes is multipart/form-data for file parameter types
      consumesList = Arrays.asList("multipart/form-data");
    }

    if (producesList.isEmpty()) {
      producesList.add(MediaType.ALL_VALUE);
    }
    if (consumesList.isEmpty()) {
      consumesList.add(MediaType.APPLICATION_JSON_VALUE);
    }
    context.put("consumes", consumesList);
    context.put("produces", producesList);
  }

  @Override
  public void apply(ApiListingContext context) {
    RequestMapping annotation = findAnnotation(context.getResourceGroup().getControllerClass(), RequestMapping.class);
    if (annotation != null) {
      context.apiListingBuilder()
              .appendProduces(newArrayList(annotation.produces()))
              .appendConsumes(newArrayList(annotation.consumes()));
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private boolean handlerMethodHasFileParameter(RequestMappingContext context) {

    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);
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
