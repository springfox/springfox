/*
 *
 *  Copyright 2015-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.scanners;

import com.google.common.base.Optional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.OperationContext;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MediaTypeReader implements OperationBuilderPlugin, ApiListingBuilderPlugin {

  @Override
  public void apply(OperationContext context) {

    DocumentationContext documentationContext = context.getDocumentationContext();

    Set<String> operationConsumesList = toSet(context.consumes());
    Set<String> operationProducesList = toSet(context.produces());

    if (handlerMethodHasFileParameter(context)) {
      operationConsumesList = newHashSet(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    if (operationProducesList.isEmpty() && documentationContext.getProduces().isEmpty()) {
      operationProducesList.add(MediaType.ALL_VALUE);
    }
    if (operationConsumesList.isEmpty() && documentationContext.getConsumes().isEmpty()) {
      operationConsumesList.add(MediaType.APPLICATION_JSON_VALUE);
    }
    context.operationBuilder().consumes(operationConsumesList);
    context.operationBuilder().produces(operationProducesList);
  }

  @Override
  public void apply(ApiListingContext context) {
    Optional<? extends Class<?>> controller = context.getResourceGroup().getControllerClass();
    if (controller.isPresent()) {
      RequestMapping annotation = findAnnotation(controller.get(), RequestMapping.class);
      if (annotation != null) {
        context.apiListingBuilder()
            .appendProduces(newArrayList(annotation.produces()))
            .appendConsumes(newArrayList(annotation.consumes()));
      }
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private boolean handlerMethodHasFileParameter(OperationContext context) {

    List<ResolvedMethodParameter> methodParameters = context.getParameters();
    for (ResolvedMethodParameter resolvedMethodParameter : methodParameters) {
      if (MultipartFile.class.isAssignableFrom(resolvedMethodParameter.getParameterType().getErasedType())) {
        return true;
      }
    }
    return false;
  }

  private Set<String> toSet(Set<? extends MediaType> mediaTypeSet) {
    Set<String> mediaTypes = newHashSet();
    for (MediaType mediaType : mediaTypeSet) {
      mediaTypes.add(mediaType.toString());
    }
    return mediaTypes;
  }
}
