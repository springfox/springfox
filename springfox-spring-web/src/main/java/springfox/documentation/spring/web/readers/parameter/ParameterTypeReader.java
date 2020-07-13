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
import org.springframework.http.codec.multipart.FilePart;
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
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.spring.web.readers.parameter.ParameterTypeDeterminer.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@SuppressWarnings("deprecation")
public class ParameterTypeReader implements ParameterBuilderPlugin {
  private static final Logger LOGGER = LoggerFactory.getLogger(ParameterTypeReader.class);
  private static final List<HttpMethod> QUERY_ONLY_HTTP_METHODS = Arrays.asList(HttpMethod.GET,
      HttpMethod.OPTIONS,
      HttpMethod.HEAD);

  @Override
  public void apply(ParameterContext context) {
    String parameterType = findParameterType(context);
    context.parameterBuilder().parameterType(parameterType);
    Collection<MediaType> accepts =
        "body".equals(parameterType)
            ? Collections.singleton(MediaType.APPLICATION_JSON)
            : null;
    context.requestParameterBuilder()
        .in(parameterType)
        .accepts(accepts);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
  public static String findParameterType(ParameterContext parameterContext) {
    ResolvedMethodParameter resolvedMethodParameter = parameterContext.resolvedMethodParameter();
    ResolvedType parameterType = resolvedMethodParameter.getParameterType();
    parameterType = parameterContext.alternateFor(parameterType);

    //Multi-part file trumps any other annotations
    if (isFileType(parameterType) || isListOfFiles(parameterType)) {
      if (resolvedMethodParameter.hasParameterAnnotation(RequestPart.class)) {
        parameterContext.requestParameterBuilder()
            .accepts(Collections.singleton(MediaType.MULTIPART_FORM_DATA));
        return "formData";
      }
      parameterContext.requestParameterBuilder()
          .accepts(Collections.singleton(MediaType.APPLICATION_OCTET_STREAM));
      return "body";
    }
    if (resolvedMethodParameter.hasParameterAnnotation(PathVariable.class)) {
      return "path";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestBody.class)) {
      return "body";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestPart.class)) {
      parameterContext.requestParameterBuilder()
          .accepts(Collections.singleton(MediaType.MULTIPART_FORM_DATA));
      return "formData";
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestParam.class)) {
      return determineScalarParameterType(
          parameterContext.getOperationContext().consumes(),
          parameterContext.getOperationContext().httpMethod());
    } else if (resolvedMethodParameter.hasParameterAnnotation(RequestHeader.class)) {
      return "header";
    } else if (resolvedMethodParameter.hasParameterAnnotation(ModelAttribute.class)) {
      parameterContext.requestParameterBuilder()
          .accepts(Collections.singleton(MediaType.APPLICATION_FORM_URLENCODED));
      LOGGER.warn("@ModelAttribute annotated parameters should have already been expanded via "
          + "the ExpandedParameterBuilderPlugin");
    }
    if (!resolvedMethodParameter.hasParameterAnnotations()) {
      return determineScalarParameterType(
          parameterContext.getOperationContext().consumes(),
          parameterContext.getOperationContext().httpMethod());
    }
    return QUERY_ONLY_HTTP_METHODS.contains(parameterContext.getOperationContext().httpMethod()) ? "query" : "body";
  }

  private static boolean isListOfFiles(ResolvedType parameterType) {
    return isContainerType(parameterType) && isFileType(collectionElementType(parameterType));
  }

  private static boolean isFileType(ResolvedType parameterType) {
    return MultipartFile.class.isAssignableFrom(parameterType.getErasedType()) ||
        FilePart.class.isAssignableFrom(parameterType.getErasedType());
  }

}