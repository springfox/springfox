/*
 *
 *  Copyright 2015 the original author or authors.
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
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.lang.annotation.Annotation;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ParameterTypeReader implements ParameterBuilderPlugin {

  @Override
  public void apply(ParameterContext context) {
    MethodParameter methodParameter = context.methodParameter();
    ResolvedMethodParameter resolvedMethodParameter = context.resolvedMethodParameter();
    ResolvedType parameterType = resolvedMethodParameter.getResolvedParameterType();
    parameterType = context.alternateFor(parameterType);
    context.parameterBuilder().parameterType(findParameterType(methodParameter, parameterType));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  public static String findParameterType(MethodParameter methodParameter, ResolvedType parameterType) {
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      return "form";
    }
    Annotation[] methodAnnotations = methodParameter.getParameterAnnotations();
    for (Annotation annotation : methodAnnotations) {
      if (annotation instanceof PathVariable) {
        return "path";
      } else if (annotation instanceof ModelAttribute) {
        return "body";
      } else if (annotation instanceof RequestBody) {
        return "body";
      } else if (annotation instanceof RequestParam) {
        return "query";
      } else if (annotation instanceof RequestHeader) {
        return "header";
      }
    }
    return "body";
  }
}
