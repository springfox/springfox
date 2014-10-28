package com.mangofactory.swagger.readers.operation.parameter;

import com.mangofactory.swagger.models.SimpleProperties;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
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
  private ResolvedMethodParameter methodParameter;

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

  public SwaggerParameterBuilder withMethodParameter(ResolvedMethodParameter methodParameter) {
    this.methodParameter = methodParameter;
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
      pathParameter.setRequired(required);
      pathParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      return pathParameter;
    } else if ("header".equals(type)) {
      HeaderParameter headerParameter = new HeaderParameter();
      headerParameter.setRequired(required);
      headerParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      return headerParameter;
    } else if ("query".equals(type)) {
      QueryParameter queryParameter = new QueryParameter();
      queryParameter.setRequired(required);
      queryParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      return queryParameter;
    } else if ("formData".equals(type)) {
      FormParameter formParameter = new FormParameter();
      formParameter.setType(dataType);
      formParameter.setRequired(required);
      return formParameter;
    } else if ("cookie".equals(type)) {
      CookieParameter cookieParameter = new CookieParameter();
      cookieParameter.setRequired(required);
      cookieParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
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