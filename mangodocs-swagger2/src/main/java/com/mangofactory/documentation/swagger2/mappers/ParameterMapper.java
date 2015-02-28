package com.mangofactory.documentation.swagger2.mappers;

import com.google.common.base.Optional;
import com.mangofactory.documentation.schema.ModelRef;
import com.wordnik.swagger.models.ArrayModel;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.RefModel;
import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.CookieParameter;
import com.wordnik.swagger.models.parameters.FormParameter;
import com.wordnik.swagger.models.parameters.HeaderParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.PathParameter;
import com.wordnik.swagger.models.parameters.QueryParameter;
import com.wordnik.swagger.models.parameters.SerializableParameter;
import com.wordnik.swagger.models.properties.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mangofactory.documentation.swagger2.mappers.ModelMapper.*;


@Component
public class ParameterMapper {

  @Autowired
  protected ModelMapper modelMapper;

  public Parameter toSwagger2Parameter(com.mangofactory.documentation.service.Parameter source) {
    Parameter parameter = bodyParameter(source);
    return serializableParameter(source).or(parameter);
  }

  private Parameter bodyParameter(com.mangofactory.documentation.service.Parameter source) {
    BodyParameter parameter = new BodyParameter()
            .description(source.getDescription())
            .name(source.getName())
            .schema(fromModelRef(source.getModelRef()));
    parameter.setAccess(source.getParamAccess());
    parameter.setRequired(source.isRequired());
    return parameter;
  }

  private Model fromModelRef(ModelRef modelRef) {
    if (modelRef.isCollection()) {
      return new ArrayModel().items(property(modelRef.getItemType()));
    }
    return new RefModel(modelRef.getType());
  }

  private Optional<Parameter> serializableParameter(com.mangofactory.documentation.service.Parameter source) {
    SerializableParameter toReturn;
    if ("header".equalsIgnoreCase(source.getParamType())) {
      HeaderParameter param = new HeaderParameter();
      param.setDefaultValue(source.getDefaultValue());
      toReturn = param;
    } else if ("form".equalsIgnoreCase(source.getParamType())) {
      FormParameter param = new FormParameter();
      param.setDefaultValue(source.getDefaultValue());
      toReturn = param;
    } else if ("path".equalsIgnoreCase(source.getParamType())) {
      PathParameter param = new PathParameter();
      param.setDefaultValue(source.getDefaultValue());
      toReturn = param;
    } else if ("query".equalsIgnoreCase(source.getParamType())) {
      QueryParameter param = new QueryParameter();
      param.setDefaultValue(source.getDefaultValue());
      toReturn = param;
    } else if ("cookie".equalsIgnoreCase(source.getParamType())) {
      CookieParameter param = new CookieParameter();
      param.setDefaultValue(source.getDefaultValue());
      toReturn = param;
    } else {
      return Optional.absent();
    }
    ModelRef paramModel = source.getModelRef();
    toReturn.setName(source.getName());
    toReturn.setDescription(source.getDescription());
    toReturn.setAccess(source.getParamAccess());
    toReturn.setRequired(source.isRequired());
    if (paramModel.isCollection()) {
      toReturn.setCollectionFormat("csv");
      toReturn.setType("array");
      toReturn.setItems(property(paramModel.getItemType()));
    } else {
      Property property = property(paramModel.getType());
      toReturn.setType(property.getType());
      toReturn.setFormat(property.getFormat());
    }
    return Optional.<Parameter>of(toReturn);
  }

}
