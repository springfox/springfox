/*
 *
 *  Copyright 2017-2019 the original author or authors.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerCombiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static springfox.documentation.RequestHandler.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.spi.service.contexts.Orderings.byOperationName;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;

class DefaultRequestHandlerCombiner implements RequestHandlerCombiner {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestHandlerCombiner.class);
  private static final PathAndParametersEquivalence EQUIVALENCE = new PathAndParametersEquivalence();

  public List<RequestHandler> combine(List<RequestHandler> source) {
    List<RequestHandler> combined = new ArrayList<RequestHandler>();
    Map<String, List<RequestHandler>> byPath = new HashMap<>();
    LOGGER.debug("Total number of request handlers {}", nullToEmptyList(source).size());
    for (RequestHandler each : nullToEmptyList(source)) {
      String pathKey = sortedPaths(each.getPatternsCondition());
      LOGGER.debug("Adding key: {}, {}", pathKey, each.toString());
      byPath.putIfAbsent(pathKey, new ArrayList<>());
      byPath.get(pathKey).add(each);
    }
    for (String key : byPath.keySet()) {
      combined.addAll(combined(byPath.get(key)));
    }
    LOGGER.debug("Combined number of request handlers {}", combined.size());
    return combined.stream()
        .sorted(byPatternsCondition())
        .collect(toList());
  }

  private Collection<RequestHandler> combined(Collection<RequestHandler> requestHandlers) {
    List<RequestHandler> source = new ArrayList<>(requestHandlers);
    if (source.size() == 0 || source.size() == 1) {
      return requestHandlers;
    }
    Map<PathAndParametersEquivalence.Wrapper, List<RequestHandler>> groupByEquality = safeGroupBy(source);
    List<RequestHandler> combined = new ArrayList<>();
    groupByEquality.keySet().stream().sorted(wrapperComparator()).forEachOrdered(path -> {
      List<RequestHandler> handlers = groupByEquality.get(path);

      RequestHandler toCombine = path.get();
      if (handlers.size() > 1) {
        for (RequestHandler each : handlers) {
          if (each.equals(toCombine)) {
            continue;
          }
          //noinspection ConstantConditions
          LOGGER.debug("Combining {} and {}", toCombine.toString(), each.toString());
          toCombine = combine(toCombine, each);
        }
      }
      combined.add(toCombine);
    });
    return combined;
  }

  private Comparator<PathAndParametersEquivalence.Wrapper> wrapperComparator() {
    return (first, second) -> byPatternsCondition()
        .thenComparing(byOperationName())
        .compare(first.get(), second.get());
  }

  private Map<PathAndParametersEquivalence.Wrapper, List<RequestHandler>> safeGroupBy(
      List<RequestHandler> source) {
    try {
      return source.stream()
          .collect(groupingBy(EQUIVALENCE::wrap, LinkedHashMap::new, toList()));
    } catch (Exception e) {
      LOGGER.error("Unable to index request handlers {}. Request handlers with issues{}",
          e.getMessage(),
          keys(source));
      return Collections.emptyMap();
    }
  }

  private String keys(List<RequestHandler> source) {
    final StringBuilder sb = new StringBuilder("Request Handlers with duplicate keys {");
    for (int i = 0; i < source.size(); i++) {
      sb.append('\t')
          .append(i)
          .append(". ")
          .append(source.get(i).key());
    }
    sb.append('}');
    return sb.toString();
  }


  private RequestHandler combine(RequestHandler first, RequestHandler second) {
    if (first.compareTo(second) < 0) {
      return new CombinedRequestHandler(first, second);
    }
    return new CombinedRequestHandler(second, first);
  }
}
