package com.mangofactory.documentation.spring.web.scanners;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ApiListingContext;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.mangofactory.documentation.spring.web.readers.operation.HandlerMethodResolver;
import com.mangofactory.documentation.service.model.ResolvedMethodParameter;
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
public class MediaTypeReader implements OperationBuilderPlugin, ApiListingBuilderPlugin {

  private final TypeResolver typeResolver;

  @Autowired
  public MediaTypeReader(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  @Override
  public void apply(OperationContext context) {

    RequestMappingInfo requestMappingInfo = context.getRequestMappingInfo();
    ConsumesRequestCondition consumesCondition = requestMappingInfo.getConsumesCondition();
    ProducesRequestCondition producesRequestCondition = requestMappingInfo.getProducesCondition();

    Set<MediaType> consumesMediaTypes = consumesCondition.getConsumableMediaTypes();
    Set<MediaType> producesMediaTypes = producesRequestCondition.getProducibleMediaTypes();

    List<String> consumesList = toList(consumesMediaTypes);
    List<String> producesList = toList(producesMediaTypes);

    if (handlerMethodHasFileParameter(context)) {
      consumesList = Arrays.asList("multipart/form-data");
    }

    if (producesList.isEmpty()) {
      producesList.add(MediaType.ALL_VALUE);
    }
    if (consumesList.isEmpty()) {
      consumesList.add(MediaType.APPLICATION_JSON_VALUE);
    }
    context.operationBuilder().consumes(consumesList);
    context.operationBuilder().produces(producesList);
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

  private boolean handlerMethodHasFileParameter(OperationContext context) {

    HandlerMethodResolver handlerMethodResolver = new HandlerMethodResolver(typeResolver);
    List<ResolvedMethodParameter> methodParameters = handlerMethodResolver.methodParameters(context.getHandlerMethod());

    for (ResolvedMethodParameter resolvedMethodParameter : methodParameters) {
      if (MultipartFile.class.isAssignableFrom(resolvedMethodParameter.getResolvedParameterType().getErasedType())) {
        return true;
      }
    }
    return false;
  }

  private List<String> toList(Set<MediaType> mediaTypeSet) {
    List<String> mediaTypes = newArrayList();
    for (MediaType mediaType : mediaTypeSet) {
      mediaTypes.add(mediaType.toString());
    }
    return mediaTypes;
  }
}
