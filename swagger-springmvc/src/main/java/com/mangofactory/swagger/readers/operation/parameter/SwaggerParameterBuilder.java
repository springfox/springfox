package com.mangofactory.swagger.readers.operation.parameter;

import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.CookieParameter;
import com.wordnik.swagger.models.parameters.FormParameter;
import com.wordnik.swagger.models.parameters.HeaderParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.PathParameter;
import com.wordnik.swagger.models.parameters.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerParameterBuilder {
  private static final Logger log = LoggerFactory.getLogger(SwaggerParameterBuilder.class);
  private String type;
  private String name;
  private Boolean required;
  private String description;
  private String dataType;
  private String defaultValue;

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

  public SwaggerParameterBuilder withDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public Parameter build() {
    Parameter parameter = fromParamType();
    parameter.setDescription(description);
    parameter.setName(name);
    parameter.setIn(type);
    return parameter;
  }

  private Parameter fromParamType() {
    if ("path".equals(type)) {
      PathParameter pathParameter = new PathParameter();
      pathParameter.setType(dataType);
      pathParameter.setRequired(required);
      return pathParameter;
    } else if ("header".equals(type)) {
      HeaderParameter headerParameter = new HeaderParameter();
      headerParameter.setType(dataType);
      headerParameter.setRequired(required);

      return headerParameter;
    } else if ("query".equals(type)) {
      QueryParameter queryParameter = new QueryParameter();
      queryParameter.setType(dataType);
      queryParameter.setRequired(required);
      return queryParameter;
    } else if ("formData".equals(type)) {
      FormParameter formParameter = new FormParameter();
      formParameter.setType(dataType);
      formParameter.setRequired(required);
      return formParameter;
    } else if ("cookie".equals(type)) {
      CookieParameter cookieParameter = new CookieParameter();
      cookieParameter.setType(dataType);
      cookieParameter.setRequired(required);
      return cookieParameter;
    } else if ("body".equals(type)) {
      BodyParameter bodyParameter = new BodyParameter();
      bodyParameter.setRequired(required);
      return bodyParameter;
    } else {
      throw new IllegalArgumentException(String.format("Parameter type [%s] not supported", type));
    }
  }
}