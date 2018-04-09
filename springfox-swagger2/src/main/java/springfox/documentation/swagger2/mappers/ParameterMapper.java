/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.swagger2.mappers;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.Property;
import org.mapstruct.Mapper;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelReference;

import java.util.Collection;
import java.util.Map.Entry;

import static springfox.documentation.schema.Types.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;
import static springfox.documentation.swagger2.mappers.Properties.*;

@Mapper
public class ParameterMapper {

  private static final VendorExtensionsMapper vendorMapper = new VendorExtensionsMapper();

  public Parameter mapParameter(springfox.documentation.service.Parameter source) {
    Parameter bodyParameter = bodyParameter(source);
    return SerializableParameterFactories.create(source).or(bodyParameter);
  }

  private Parameter bodyParameter(springfox.documentation.service.Parameter source) {
    BodyParameter parameter = new BodyParameter()
        .description(source.getDescription())
        .name(source.getName())
        .schema(fromModelRef(source.getModelRef()));
    parameter.setIn(source.getParamType());
    parameter.setAccess(source.getParamAccess());
    parameter.setPattern(source.getPattern());
    parameter.setRequired(source.isRequired());
    parameter.getVendorExtensions().putAll(vendorMapper.mapExtensions(source.getVendorExtentions()));
    for (Entry<String, Collection<Example>> each : source.getExamples().asMap().entrySet()) {
      Optional<Example> example = FluentIterable.from(each.getValue()).first();
      if (example.isPresent() && example.get().getValue() != null) {
        parameter.addExample(each.getKey(), String.valueOf(example.get().getValue()));
      }
    }

    //TODO: swagger-core Body parameter does not have an enum property
    return parameter;
  }

  Model fromModelRef(ModelReference modelRef) {
    if (modelRef.isCollection()) {
      if (modelRef.getItemType().equals("byte")) {
        ModelImpl baseModel = new ModelImpl();
        baseModel.setType("string");
        baseModel.setFormat("byte");
        return maybeAddAllowableValuesToParameter(baseModel, modelRef.getAllowableValues());
      } else if (modelRef.getItemType().equals("file")) {
        ArrayModel files = new ArrayModel();
        files.items(new FileProperty());
        return files;
      }
      ModelReference itemModel = modelRef.itemModel().get();
      return new ArrayModel()
          .items(maybeAddAllowableValues(itemTypeProperty(itemModel), itemModel.getAllowableValues()));
    }
    if (modelRef.isMap()) {
      ModelImpl baseModel = new ModelImpl();
      ModelReference itemModel = modelRef.itemModel().get();
      baseModel.additionalProperties(
          maybeAddAllowableValues(
              itemTypeProperty(itemModel),
              itemModel.getAllowableValues()));
      return baseModel;
    }
    if (isBaseType(modelRef.getType())) {
      Property property = property(modelRef.getType());
      ModelImpl baseModel = new ModelImpl();
      baseModel.setType(property.getType());
      baseModel.setFormat(property.getFormat());
      return maybeAddAllowableValuesToParameter(baseModel, modelRef.getAllowableValues());

    }
    return new RefModel(modelRef.getType());
  }
}
