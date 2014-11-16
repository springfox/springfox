package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.base.Optional;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.models.SimpleProperties;
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import com.wordnik.swagger.models.Model;
import com.wordnik.swagger.models.parameters.BodyParameter;
import com.wordnik.swagger.models.parameters.CookieParameter;
import com.wordnik.swagger.models.parameters.FormParameter;
import com.wordnik.swagger.models.parameters.HeaderParameter;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.PathParameter;
import com.wordnik.swagger.models.parameters.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mangofactory.swagger.models.ModelContext.inputParam;

public class SwaggerParameterBuilder {
  private static final Logger log = LoggerFactory.getLogger(SwaggerParameterBuilder.class);
  private String type;
  private String name;
  private Boolean required;
  private String description;
  private String dataType;
  private String defaultValue;
  private ResolvedMethodParameter methodParameter;
  private ModelProvider modelProvider;

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

  public SwaggerParameterBuilder withModelProvider(ModelProvider modelProvider) {
    this.modelProvider = modelProvider;
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
      pathParameter.setDefaultValue(defaultValue);
      return pathParameter;
    } else if ("header".equals(type)) {
      HeaderParameter headerParameter = new HeaderParameter();
      headerParameter.setRequired(required);
      headerParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      headerParameter.setDefaultValue(defaultValue);
      return headerParameter;
    } else if ("query".equals(type)) {
      QueryParameter queryParameter = new QueryParameter();
      queryParameter.setRequired(required);
      queryParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      queryParameter.setDefaultValue(defaultValue);
      return queryParameter;
    } else if ("formData".equals(type)) {
      FormParameter formParameter = new FormParameter();
      formParameter.setType(dataType);
      formParameter.setRequired(required);
      formParameter.setDefaultValue(defaultValue);
      return formParameter;
    } else if ("cookie".equals(type)) {
      CookieParameter cookieParameter = new CookieParameter();
      cookieParameter.setRequired(required);
      cookieParameter.setProperty(SimpleProperties.fromType(methodParameter.getResolvedParameterType()));
      cookieParameter.setDefaultValue(defaultValue);
      return cookieParameter;
    } else if ("body".equals(type)) {
      BodyParameter bodyParameter = new BodyParameter();
      bodyParameter.setRequired(required);
      Optional<Model> modelOptional = modelProvider.modelFor(inputParam(methodParameter.getResolvedParameterType()));
      if (modelOptional.isPresent()) {
        Model model = modelOptional.get();
        bodyParameter.setSchema(model);
      } else {
        log.error("Could not generate a model for {}", methodParameter.getResolvedParameterType());
      }
      return bodyParameter;
    } else {
      throw new IllegalArgumentException(String.format("Parameter type [%s] not supported", type));
    }
  }
}