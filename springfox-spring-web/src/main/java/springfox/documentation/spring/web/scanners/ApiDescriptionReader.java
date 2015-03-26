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

package springfox.documentation.spring.web.scanners;

import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiDescriptionBuilder;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.Paths;
import springfox.documentation.spring.web.readers.operation.ApiOperationReader;

import java.util.List;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;

@Component
public class ApiDescriptionReader {

  private final ApiOperationReader operationReader;

  @Autowired
  public ApiDescriptionReader(ApiOperationReader operationReader) {
    this.operationReader = operationReader;
  }

  public List<ApiDescription> read(RequestMappingContext outerContext) {
    RequestMappingInfo requestMappingInfo = outerContext.getRequestMappingInfo();
    HandlerMethod handlerMethod = outerContext.getHandlerMethod();
    PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
    ApiSelector selector = outerContext.getDocumentationContext().getApiSelector();
    PathProvider pathProvider = outerContext.getDocumentationContext().getPathProvider();

    List<ApiDescription> apiDescriptionList = newArrayList();
    for (String pattern : matchingPaths(selector, patternsCondition)) {
        String cleanedRequestMappingPath = Paths.sanitizeRequestMappingPattern(pattern);
        String path = pathProvider.getOperationPath(cleanedRequestMappingPath);
        String methodName = handlerMethod.getMethod().getName();
        RequestMappingContext operationContext = outerContext.copyPatternUsing(cleanedRequestMappingPath);
        apiDescriptionList.add(
                new ApiDescriptionBuilder(outerContext.operationOrdering())
                        .path(path)
                        .description(methodName)
                        .operations(operationReader.read(operationContext))
                        .hidden(false)
                        .build());
    }
    return apiDescriptionList;
  }

    private FluentIterable<String> matchingPaths(ApiSelector selector,
      PatternsRequestCondition patternsCondition) {
        return from(patternsCondition.getPatterns())
                .filter(selector.getPathSelector());
    }

}
