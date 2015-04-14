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

import com.wordnik.swagger.models.ArrayModel;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.ModelImpl;
import com.wordnik.swagger.models.RefModel;
import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelRef;

import static springfox.documentation.schema.Types.*;


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

  Model fromModelRef(ModelRef modelRef) {
    if (modelRef.isCollection()) {
      return new ArrayModel().items(Properties.property(modelRef.getItemType()));
    }
    if (modelRef.isMap()) {
      ModelImpl baseModel = new ModelImpl();
      baseModel.additionalProperties(Properties.property(modelRef.getItemType()));
      return baseModel;
    }
    if (isBaseType(modelRef.getType())) {
      ModelImpl baseModel = new ModelImpl();
      baseModel.setType(modelRef.getType());
      return baseModel;
    }
    return new RefModel(modelRef.getType());
  }

}
