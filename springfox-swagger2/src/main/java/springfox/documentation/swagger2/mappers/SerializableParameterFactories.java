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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.CookieParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.SerializableParameter;
import io.swagger.models.properties.Property;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;

import java.util.Map;

import static com.google.common.base.Functions.*;
import static springfox.documentation.swagger2.mappers.Properties.*;

public class SerializableParameterFactories {
  public static final Map<String, SerializableParameterFactory> factory = ImmutableMap.<String,
      SerializableParameterFactory>builder()
      .put("header", new HeaderSerializableParameterFactory())
      .put("form", new FormSerializableParameterFactory())
      .put("path", new PathSerializableParameterFactory())
      .put("query", new QuerySerializableParameterFactory())
      .put("cookie", new CookieSerializableParameterFactory())
      .build();

  private SerializableParameterFactories() {
    throw new UnsupportedOperationException();
  }

  static Optional<io.swagger.models.parameters.Parameter> create(Parameter source) {
    SerializableParameterFactory factory = forMap(SerializableParameterFactories.factory, new
        NullSerializableParameterFactory())
        .apply(source.getParamType().toLowerCase());

    SerializableParameter toReturn = factory.create(source);
    if (toReturn == null) {
      return Optional.absent();
    }
    ModelReference paramModel = source.getModelRef();
    toReturn.setName(source.getName());
    toReturn.setDescription(source.getDescription());
    toReturn.setAccess(source.getParamAccess());
    toReturn.setRequired(source.isRequired());
    if (paramModel.isCollection()) {
      toReturn.setCollectionFormat("multi");
      toReturn.setType("array");
      toReturn.setItems(itemTypeProperty(paramModel.itemModel().get()));
    } else {
      //TODO: remove this downcast when swagger-core fixes its problem
      ((AbstractSerializableParameter) toReturn).setDefaultValue(source.getDefaultValue());
      Property property = property(paramModel.getType());
      toReturn.setType(property.getType());
      toReturn.setFormat(property.getFormat());
    }
    maybeAddAlllowableValues(source, toReturn);
    return Optional.of((io.swagger.models.parameters.Parameter) toReturn);
  }

  private static void maybeAddAlllowableValues(Parameter source, SerializableParameter toReturn) {
    if (source.getAllowableValues() instanceof AllowableListValues) {
      AllowableListValues allowableValues = (AllowableListValues) source.getAllowableValues();
      toReturn.setEnum(allowableValues.getValues());
    }
  }

  static class CookieSerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      CookieParameter param = new CookieParameter();
      param.setDefaultValue(source.getDefaultValue());
      return param;
    }
  }

  static class FormSerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      FormParameter param = new FormParameter();
      param.setDefaultValue(source.getDefaultValue());
      return param;
    }
  }

  static class HeaderSerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      HeaderParameter param = new HeaderParameter();
      param.setDefaultValue(source.getDefaultValue());
      return param;
    }
  }

  static class PathSerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      PathParameter param = new PathParameter();
      param.setDefaultValue(source.getDefaultValue());
      return param;
    }
  }

  static class QuerySerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      QueryParameter param = new QueryParameter();
      param.setDefaultValue(source.getDefaultValue());
      return param;
    }
  }

  static class NullSerializableParameterFactory implements SerializableParameterFactory {
    @Override
    public SerializableParameter create(Parameter source) {
      return null;
    }
  }
}
