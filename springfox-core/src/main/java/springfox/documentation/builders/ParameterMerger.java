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

import springfox.documentation.service.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.Parameters.*;

class ParameterMerger {

  private final List<Parameter> destination;
  private final List<Parameter> source;

  ParameterMerger(List<Parameter> destination, List<Parameter> source) {
    this.destination = new ArrayList<>(destination);
    this.source = new ArrayList<>(source);
  }

  public List<Parameter> merged() {
    Set<String> existingParameterNames = destination.stream().map(toParameterName()).collect(toSet());
    Set<String> newParameterNames = source.stream().map(toParameterName()).collect(toSet());
    List<Parameter> merged = new ArrayList<>();

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

  private List<Parameter> asIsParameters(Set<String> asIsParams, List<Parameter> source) {
    List<Parameter> parameters = new ArrayList<>();
    for (Parameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<Parameter> mergedParameters(
      Set<String> paramsToMerge,
      List<Parameter> existingParameters,
      List<Parameter> newParams) {
    List<Parameter> parameters = new ArrayList<>();
    for (Parameter newParam : newParams) {
      Optional<Parameter> original = existingParameters.stream().filter(withName(newParam.getName())).findFirst();
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
        .order(source.getOrder())
        .scalarExample(source.getScalarExample())
        .complexExamples(source.getExamples())
        .collectionFormat(source.getCollectionFormat())
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
