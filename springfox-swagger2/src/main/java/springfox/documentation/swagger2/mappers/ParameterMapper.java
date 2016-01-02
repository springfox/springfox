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

package springfox.documentation.swagger2.mappers;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;

import static springfox.documentation.schema.Types.*;
import static springfox.documentation.swagger2.mappers.Properties.*;


@Mapper
public class ParameterMapper {

  public Parameter mapParameter(springfox.documentation.service.Parameter source) {
    Parameter bodyParameter = bodyParameter(source);
    return SerializableParameterFactories.create(source).or(bodyParameter);
  }

  private Parameter bodyParameter(springfox.documentation.service.Parameter source) {
    BodyParameter parameter = new BodyParameter()
        .description(source.getDescription())
        .name(source.getName())
        .schema(fromModelRef(source.getModelRef()));
    parameter.setAccess(source.getParamAccess());
    parameter.setRequired(source.isRequired());
    return parameter;
  }

  Model fromModelRef(ModelReference modelRef) {
    if (modelRef.isCollection()) {
      return new ArrayModel()
          .items(itemTypeProperty(modelRef.itemModel().get()));
    }
    if (modelRef.isMap()) {
      ModelImpl baseModel = new ModelImpl();
      baseModel.additionalProperties(property(modelRef.getItemType()));
      return baseModel;
    }
    if (isBaseType(modelRef.getType())) {
      Property property = property(modelRef.getType());
      ModelImpl baseModel = new ModelImpl();
      baseModel.setType(property.getType());
      baseModel.setFormat(property.getFormat());
      if (modelRef.getAllowableValues() instanceof AllowableListValues) {
        AllowableListValues allowableValues = (AllowableListValues) modelRef.getAllowableValues();
        baseModel.setEnum(allowableValues.getValues());
      }
      return baseModel;
    }
    return new RefModel(modelRef.getType());
  }
}
