/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.spring.web.plugins;

import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;
import static springfox.documentation.spi.service.contexts.Orderings.patternsCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerCombiner;

class DefaultRequestHandlerCombiner implements RequestHandlerCombiner {

  public List<RequestHandler> combine(List<RequestHandler> source) {
    List<RequestHandler> combined = new ArrayList<RequestHandler>();
    Map<String, List<RequestHandler>> byPath = new LinkedHashMap<>();
    for (RequestHandler each : nullToEmptyList(source)) {
      byPath.computeIfAbsent(patternsCondition(each).toString(), k -> new ArrayList<>()).add(each);
    }
    for (String key : byPath.keySet()) {
      combined.addAll(combined(byPath.get(key)));
    }
    return combined.stream().sorted(byPatternsCondition()).collect(Collectors.toList());
  }

  private Collection<? extends RequestHandler> combined(Collection<RequestHandler> requestHandlers) {
    List<RequestHandler> source = new ArrayList<>(requestHandlers);
    if (source.size() == 0 || source.size() == 1) {
      return requestHandlers;
    }

    Map<PathAndParametersEquivalence, List<RequestHandler>> groupByEquality = new LinkedHashMap<>();
    for (RequestHandler requestHandler : source) {
      groupByEquality.computeIfAbsent(new PathAndParametersEquivalence(requestHandler), k -> new ArrayList<>())
          .add(requestHandler);
    }
    List<RequestHandler> combined = new ArrayList<>();
    for (PathAndParametersEquivalence path : groupByEquality.keySet()) {
      List<RequestHandler> handlers = groupByEquality.get(path);

      RequestHandler toCombine = path.get();
      if (handlers.size() > 1) {
        for (RequestHandler each : handlers) {
          if (each.equals(toCombine)) {
            continue;
          }
          toCombine = combine(toCombine, each);
        }
      }
      combined.add(toCombine);
    }
    return combined;
  }

  private RequestHandler combine(RequestHandler first, RequestHandler second) {
    return new CombinedRequestHandler(first, second);
  }

}
