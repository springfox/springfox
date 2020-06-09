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

package springfox.documentation.builders;

import springfox.documentation.service.RequestParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

//TODO: write a test for this
class RequestParameterMerger {

  private final Map<String, RequestParameter> destination;
  private final Map<String, RequestParameter> source;

  RequestParameterMerger(
      Collection<RequestParameter> destination,
      Collection<RequestParameter> source) {

    this.destination = destination.stream()
        .collect(toMap(RequestParameter::getName, Function.identity()));
    this.source = source.stream()
        .collect(toMap(
            RequestParameter::getName,
            Function.identity(),
            this::mergeWithPrecedence));
  }

  public List<RequestParameter> merge() {
    List<RequestParameter> merged = new ArrayList<>();

    Set<String> asIsParams = destination.keySet().stream()
        .filter(entry -> !source.containsKey(entry))
        .collect(toSet());
    Set<String> missingParamNames = source.keySet().stream()
        .filter(entry -> !destination.containsKey(entry))
        .collect(toSet());
    Set<String> paramsToMerge = source.keySet().stream()
        .filter(destination::containsKey)
        .collect(toSet());

    merged.addAll(parametersNotRequiringMerging(asIsParams, destination.values()));
    merged.addAll(parametersNotRequiringMerging(missingParamNames, source.values()));
    merged.addAll(mergedParameters(paramsToMerge));
    return merged;
  }

  private List<RequestParameter> parametersNotRequiringMerging(
      Set<String> asIsParams,
      Collection<RequestParameter> source) {
    List<RequestParameter> parameters = new ArrayList<>();
    for (RequestParameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<RequestParameter> mergedParameters(
      Set<String> paramsToMerge) {
    List<RequestParameter> parameters = new ArrayList<>();

    for (String each : paramsToMerge) {
      RequestParameter original = destination.get(each);
      RequestParameter newParam = source.get(each);
      parameters.add(mergeWithPrecedence(newParam, original));
    }
    return parameters;
  }

  private RequestParameter merge(
      RequestParameter destination,
      RequestParameter source) {
    return new RequestParameterBuilder()
        .copyOf(destination)
        .copyOf(source)
        .build();
  }

  private RequestParameter mergeWithPrecedence(
      RequestParameter first,
      RequestParameter second) {
    if (first.getPrecedence() > second.getPrecedence()) {
      return merge(first, second);
    } else {
      return merge(second, first);
    }
  }
}
