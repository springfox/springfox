/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.PathContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.OperationReader;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

@Component
public class ApiDescriptionReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiDescriptionReader.class);
  private final OperationReader operationReader;
  private final DocumentationPluginsManager pluginsManager;
  private final ApiDescriptionLookup lookup;

  @Autowired
  public ApiDescriptionReader(
      @Qualifier("cachedOperations") OperationReader operationReader,
      DocumentationPluginsManager pluginsManager,
      ApiDescriptionLookup lookup) {
    this.operationReader = operationReader;
    this.pluginsManager = pluginsManager;
    this.lookup = lookup;
  }

  public List<ApiDescription> read(RequestMappingContext outerContext) {
    PatternsRequestCondition patternsCondition = outerContext.getPatternsCondition();
    ApiSelector selector = outerContext.getDocumentationContext().getApiSelector();

    List<ApiDescription> apiDescriptionList = new ArrayList<>();
    for (String path : matchingPaths(selector, patternsCondition)) {
      String methodName = outerContext.getName();
      try {
        RequestMappingContext operationContext = outerContext.copyPatternUsing(path)
            .withKnownModels(outerContext.getModelMap());

        List<Operation> operations = operationReader.read(operationContext);
        if (operations.size() > 0) {
          operationContext.apiDescriptionBuilder()
              .groupName(outerContext.getGroupName())
              .operations(operations)
              .pathDecorator(pluginsManager.decorator(new PathContext(outerContext, operations.stream().findFirst())))
              .path(path)
              .description(methodName)
              .hidden(false);
          ApiDescription apiDescription = operationContext.apiDescriptionBuilder().build();
          lookup.add(outerContext.key(), apiDescription);
          apiDescriptionList.add(apiDescription);
        }
      } catch (Error e) {
        String contentMsg = "Skipping process path[" + path + "], method[" + methodName + "] as it has an error.";
        LOGGER.error(contentMsg, e);
      }
    }
    return apiDescriptionList;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<String> matchingPaths(ApiSelector selector, PatternsRequestCondition patternsCondition) {
    return ((Set<String>) patternsCondition.getPatterns()).stream()
        .filter(selector.getPathSelector())
        .sorted(naturalOrder())
        .collect(toList());
  }

}