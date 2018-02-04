/*
 *
 *  Copyright 2018 the original author or authors.
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

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

import static springfox.documentation.schema.Collections.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterTypeReader implements ParameterBuilderPlugin {
  private static final Logger LOGGER = LoggerFactory.getLogger(ParameterTypeReader.class);

  @Override
  public void apply(ParameterContext context) {
    context.parameterBuilder().parameterType(findParameterType(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  public static String findParameterType(ParameterContext parameterContext) {
    ResolvedMethodParameter resolvedMethodParameter = parameterContext.resolvedMethodParameter();
    ResolvedType parameterType = resolvedMethodParameter.getParameterType();
    parameterType = parameterContext.alternateFor(parameterType);

    //Multi-part file trumps any other annotations
    if (isFileType(parameterType) || isListOfFiles(parameterType)) {
      return "form";
    }
    if (resolvedMethodParameter.hasParameterAnnotation(PathVariable.class)) {
      return "path";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestBody.class)) {
      return "body";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestPart.class)) {
      return "formData";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestParam.class)) {
      return queryOrForm(parameterContext.getOperationContext());
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestHeader.class)) {
      return "header";
    } else if (resolvedMethodParameter.hasParameterAnnotation(ModelAttribute.class)) {
      LOGGER.warn("@ModelAttribute annotated parameters should have already been expanded via "
          + "the ExpandedParameterBuilderPlugin");
    }
    if (!resolvedMethodParameter.hasParameterAnnotations()) {
      return queryOrForm(parameterContext.getOperationContext());
    }
    return "body";
  }

  private static boolean isListOfFiles(ResolvedType parameterType) {
    return isContainerType(parameterType) && isFileType(collectionElementType(parameterType));
  }

  private static boolean isFileType(ResolvedType parameterType) {
    return MultipartFile.class.isAssignableFrom(parameterType.getErasedType());
  }

  private static String queryOrForm(OperationContext context) {
    if (context.consumes().contains(MediaType.APPLICATION_FORM_URLENCODED) && context.httpMethod() == HttpMethod
        .POST) {
      return "form";
    }
    return "query";
  }
}