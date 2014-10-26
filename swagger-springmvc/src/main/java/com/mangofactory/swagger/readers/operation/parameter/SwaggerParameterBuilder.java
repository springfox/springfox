package com.mangofactory.swagger.readers.operation.parameter;

import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.PathParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerParameterBuilder {
  private static final Logger log = LoggerFactory.getLogger(SwaggerParameterBuilder.class);
  private String type;
  private String name;
  private Boolean required;
  private String description;
  private String dataType;

  public SwaggerParameterBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public SwaggerParameterBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public SwaggerParameterBuilder withRequired(Boolean required) {
    this.required = required;
    return this;
  }

  public SwaggerParameterBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public SwaggerParameterBuilder withDataType(String dataType) {
    this.dataType = dataType;
    return this;
  }

  public Parameter build() {
    Parameter parameter = fromType();
    parameter.setDescription(description);
    parameter.setName(name);
    parameter.setIn(type);
    return parameter;
  }

  private Parameter fromType() {
    if (type.equals("path")) {
      PathParameter pathParameter = new PathParameter();
      pathParameter.setType(dataType);
      pathParameter.setRequired(required);
      return pathParameter;
    } else {
      log.error("Parameter type [{}] not yet supported", type);
      return new BodyParameter();
    }
  }
}