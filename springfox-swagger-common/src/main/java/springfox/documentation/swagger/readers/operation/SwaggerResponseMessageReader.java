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

package springfox.documentation.swagger.readers.operation;

import com.google.common.base.Optional;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.annotations.Annotations;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Set;

import static com.google.common.collect.Sets.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class SwaggerResponseMessageReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    context.operationBuilder()
            .responseMessages(read(handlerMethod));

  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  protected Set<ResponseMessage> read(HandlerMethod handlerMethod) {
    Optional<ApiResponses> apiResponsesOptional = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
    Set<ResponseMessage> responseMessages = newHashSet();
    if (apiResponsesOptional.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        String overrideTypeName = overrideTypeName(apiResponse);

        responseMessages.add(new ResponseMessageBuilder()
                .code(apiResponse.code())
                .message(apiResponse.message())
                .responseModel(new ModelRef(overrideTypeName))
                .build());
      }
    }
    return responseMessages;
  }


  private String overrideTypeName(ApiResponse apiResponse) {
    if (apiResponse.response() != null) {
      return apiResponse.response().getSimpleName();
    }
    return "";
  }

}
