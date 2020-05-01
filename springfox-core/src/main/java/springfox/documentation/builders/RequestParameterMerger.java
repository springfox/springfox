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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.*;

//TODO: write a test for this
class RequestParameterMerger {

  private final List<RequestParameter> destination;
  private final List<RequestParameter> source;

  RequestParameterMerger(List<RequestParameter> destination, List<RequestParameter> source) {
    this.destination = new ArrayList<>(destination);
    this.source = new ArrayList<>(source);
  }

  public List<RequestParameter> merged() {
    Set<String> existingParameterNames = destination.stream().map(RequestParameter::getName).collect(toSet());
    Set<String> newParameterNames = source.stream().map(RequestParameter::getName).collect(toSet());
    List<RequestParameter> merged = new ArrayList<>();

    Set<String> asIsParams = existingParameterNames.stream()
        .filter(entry -> !newParameterNames.contains(entry)).collect(toSet());
    Set<String> missingParamNames = newParameterNames.stream()
        .filter(entry -> !existingParameterNames.contains(entry)).collect(toSet());
    Set<String> paramsToMerge = newParameterNames.stream()
        .filter(existingParameterNames::contains).collect(toSet());

    merged.addAll(asIsParameters(asIsParams, destination));
    merged.addAll(newParameters(missingParamNames, source));
    merged.addAll(mergedParameters(paramsToMerge, destination, source));
    return merged;
  }

  private List<RequestParameter> asIsParameters(Set<String> asIsParams, List<RequestParameter> source) {
    List<RequestParameter> parameters = new ArrayList<>();
    for (RequestParameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<RequestParameter> mergedParameters(
      Set<String> paramsToMerge,
      List<RequestParameter> existingParameters,
      List<RequestParameter> newParams) {
    List<RequestParameter> parameters = new ArrayList<>();
    for (RequestParameter newParam : newParams) {
      Optional<RequestParameter> original = existingParameters.stream()
          .filter(input -> newParam.getName().equals(input.getName()))
          .findFirst();
      if (paramsToMerge.contains(newParam.getName()) && original.isPresent()) {
        if (newParam.getOrder() > original.get().getOrder()) {
          parameters.add(merged(newParam, original.get()));
        } else {
          parameters.add(merged(original.get(), newParam));
        }
      }
    }
    return parameters;
  }

  private RequestParameter merged(RequestParameter destination, RequestParameter source) {
    return new RequestParameterBuilder()
        .copyOf(destination)
        .copyOf(source)
        .build();
  }

  private List<RequestParameter> newParameters(Set<String> missingParamNames, List<RequestParameter> newParams) {
    List<RequestParameter> parameters = new ArrayList<>();
    for (RequestParameter each : newParams) {
      if (missingParamNames.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }
}
