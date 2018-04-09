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

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.*;
import springfox.documentation.service.Parameter;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.Parameters.*;

class ParameterMerger {

  private final List<Parameter> destination;
  private final List<Parameter> source;

  public ParameterMerger(List<Parameter> destination, List<Parameter> source) {
    this.destination = newArrayList(destination);
    this.source = newArrayList(source);
  }

  public List<Parameter> merged() {
    Set<String> existingParameterNames = from(destination).transform(toParameterName()).toSet();
    Set<String> newParameterNames = from(source).transform(toParameterName()).toSet();
    List<Parameter> merged = newArrayList();

    SetView<String> asIsParams = difference(existingParameterNames, newParameterNames);
    SetView<String> missingParamNames = difference(newParameterNames, existingParameterNames);
    SetView<String> paramsToMerge = Sets.intersection(newParameterNames, existingParameterNames);

    merged.addAll(asIsParameters(asIsParams, destination));
    merged.addAll(newParameters(missingParamNames, source));
    merged.addAll(mergedParameters(paramsToMerge, destination, source));
    return merged;
  }

  private List<Parameter> asIsParameters(SetView<String> asIsParams, List<Parameter> source) {
    List<Parameter> parameters = newArrayList();
    for (Parameter each : source) {
      if (asIsParams.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }

  private List<Parameter> mergedParameters(
      SetView<String> paramsToMerge,
      List<Parameter> existingParameters,
      List<Parameter> newParams) {
    List<Parameter> parameters = newArrayList();
    for (Parameter newParam : newParams) {
      Optional<Parameter> original = from(existingParameters).firstMatch(withName(newParam.getName()));
      if (paramsToMerge.contains(newParam.getName()) && original.isPresent()) {
        if (newParam.getOrder() > original.get().getOrder()){
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
        .type(source.getType().orNull())
        .order(source.getOrder())
        .scalarExample(source.getScalarExample())
        .complexExamples(source.getExamples())
        .build();
  }

  private List<Parameter> newParameters(SetView<String> missingParamNames, List<Parameter> newParams) {
    List<Parameter> parameters = newArrayList();
    for (Parameter each : newParams) {
      if (missingParamNames.contains(each.getName())) {
        parameters.add(each);
      }
    }
    return parameters;
  }
}
