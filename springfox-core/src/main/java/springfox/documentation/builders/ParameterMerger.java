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

package springfox.documentation.builders;

import static springfox.documentation.builders.Parameters.toParameterName;
import static springfox.documentation.builders.Parameters.withName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import springfox.documentation.service.Parameter;

class ParameterMerger {

  private final List<Parameter> destination;
  private final List<Parameter> source;

  public ParameterMerger(List<Parameter> destination, List<Parameter> source) {
    this.destination = new ArrayList<>(destination);
    this.source = new ArrayList<>(source);
  }

  public List<Parameter> merged() {
    Set<String> existingParameterNames = destination.stream().map(toParameterName()).collect(Collectors.toSet());
    Set<String> newParameterNames = source.stream().map(toParameterName()).collect(Collectors.toSet());
    List<Parameter> merged = new ArrayList<>();

    Set<String> asIsParams = new HashSet<String>(existingParameterNames);
    asIsParams.removeAll(newParameterNames);
    Set<String> missingParamNames = new HashSet<String>(newParameterNames);
    missingParamNames.removeAll(existingParameterNames);
    Set<String> paramsToMerge = new HashSet<>(newParameterNames);
    paramsToMerge.retainAll(existingParameterNames);

    merged.addAll(asIsParameters(asIsParams, destination));
    merged.addAll(newParameters(missingParamNames, source));
    merged.addAll(mergedParameters(paramsToMerge, destination, source));
    return merged;
  }

  private List<Parameter> asIsParameters(Set<String> asIsParams, List<Parameter> source) {
    List<Parameter> parameters = new ArrayList<>();
    for (Parameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<Parameter> mergedParameters(Set<String> paramsToMerge,
                                           List<Parameter> existingParameters,
                                           List<Parameter> newParams) {
    List<Parameter> parameters = new ArrayList<>();
    for (Parameter newParam : newParams) {
      Optional<Parameter> original = existingParameters.stream().filter(withName(newParam.getName())).findFirst();
      if (paramsToMerge.contains(newParam.getName()) && original.isPresent()) {
        parameters.add(merged(original.get(), newParam));
      }
    }
    return parameters;
  }

  private Parameter merged(Parameter destination, Parameter source) {
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
        .build();
  }

  private List<Parameter> newParameters(Set<String> missingParamNames, List<Parameter> newParams) {
    List<Parameter> parameters = new ArrayList<>();
    for (Parameter each : newParams) {
      if (missingParamNames.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }
}
