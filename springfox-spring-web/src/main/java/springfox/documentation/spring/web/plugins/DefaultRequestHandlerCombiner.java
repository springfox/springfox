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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerCombiner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

class DefaultRequestHandlerCombiner implements RequestHandlerCombiner {
  
  public List<RequestHandler> combine(List<RequestHandler> source) {
    List<RequestHandler> combined = new ArrayList<RequestHandler>();
    Multimap<String, RequestHandler> byPath = LinkedListMultimap.create();
    for (RequestHandler each : source) {
      byPath.put(patternsCondition(each).toString(), each);
    }
    for (String key : byPath.keySet()) {
      combined.addAll(combined(byPath.get(key)));
    }
    return byPatternsCondition().sortedCopy(combined);
  }

  private Collection<? extends RequestHandler> combined(Collection<RequestHandler> requestHandlers) {
    List<RequestHandler> source = newArrayList(requestHandlers);
    SamePathDifferentMediaTypesEquivalence equality =
        new SamePathDifferentMediaTypesEquivalence();
    if (source.size() == 0 || source.size() == 1) {
      return requestHandlers;
    }
    List<RequestHandler> combined = newArrayList();
    for (Iterator<RequestHandler> outer = source.iterator(); outer.hasNext(); ) {
      RequestHandler each = outer.next();
      outer.remove();
      for (Iterator<RequestHandler> inner = source.iterator(); inner.hasNext(); ) {
        RequestHandler innerEach = inner.next();
        if (equality.equivalent(each, innerEach)) {
          each = combine(each, innerEach);
          inner.remove();
        }
      }
      combined.add(each);
    }
    return combined;
  }

  private RequestHandler combine(RequestHandler first, RequestHandler second) {
    return new CombinedRequestHandler(first, second);
  }
}
