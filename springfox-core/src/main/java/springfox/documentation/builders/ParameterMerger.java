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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * Scheduled to be removed in the next release
 * @deprecated
 * @since 3.0
 */
@Deprecated
class ParameterMerger {

  private final List<springfox.documentation.service.Parameter> destination;
  private final List<springfox.documentation.service.Parameter> source;

  ParameterMerger(
      List<springfox.documentation.service.Parameter> destination,
      List<springfox.documentation.service.Parameter> source) {
    this.destination = new ArrayList<>(destination);
    this.source = new ArrayList<>(source);
  }

  public List<springfox.documentation.service.Parameter> merged() {
    Set<String> existingParameterNames = destination.stream()
        .map(springfox.documentation.service.Parameter::getName).collect(toSet());
    Set<String> newParameterNames = source.stream()
        .map(springfox.documentation.service.Parameter::getName).collect(toSet());
    List<springfox.documentation.service.Parameter> merged = new ArrayList<>();

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

  private List<springfox.documentation.service.Parameter> asIsParameters(
      Set<String> asIsParams,
      List<springfox.documentation.service.Parameter> source) {
    List<springfox.documentation.service.Parameter> parameters = new ArrayList<>();
    for (springfox.documentation.service.Parameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<springfox.documentation.service.Parameter> mergedParameters(
      Set<String> paramsToMerge,
      List<springfox.documentation.service.Parameter> existingParameters,
      List<springfox.documentation.service.Parameter> newParams) {
    List<springfox.documentation.service.Parameter> parameters = new ArrayList<>();
    for (springfox.documentation.service.Parameter newParam : newParams) {
      Optional<springfox.documentation.service.Parameter> original = existingParameters.stream()
          .filter(input -> newParam.getName().equals(input.getName())).findFirst();
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

  private springfox.documentation.service.Parameter merged(
      springfox.documentation.service.Parameter destination,
      springfox.documentation.service.Parameter source) {
    return new ParameterBuilder()
        .from(destination)
        .name(source.getName())
        .allowableValues(source.getAllowableValues())
        .allowMultiple(source.isAllowMultiple())
        .defaultValue(source.getDefaultValue())
        .description(source.getDescription())
        .modelRef(source.getModelRef())
        .parameterAccess(source.getParamAccess())
        .parameterType(source.getParamType())
        .required(source.isRequired())
        .type(source.getType().orElse(null))
        .order(source.getOrder())
        .scalarExample(source.getScalarExample())
        .complexExamples(source.getExamples())
        .collectionFormat(source.getCollectionFormat())
        .build();
  }

  private List<springfox.documentation.service.Parameter> newParameters(
      Set<String> missingParamNames,
      List<springfox.documentation.service.Parameter> newParams) {
    List<springfox.documentation.service.Parameter> parameters = new ArrayList<>();
    for (springfox.documentation.service.Parameter each : newParams) {
      if (missingParamNames.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }
}
