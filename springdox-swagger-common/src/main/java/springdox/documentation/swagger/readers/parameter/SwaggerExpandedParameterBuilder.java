package springdox.documentation.swagger.readers.parameter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.ModelRef;
import springdox.documentation.service.AllowableListValues;
import springdox.documentation.service.AllowableValues;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springdox.documentation.spi.service.contexts.ParameterExpansionContext;
import springdox.documentation.swagger.annotations.Annotations;
import springdox.documentation.swagger.common.SwaggerPluginSupport;
import springdox.documentation.swagger.schema.ApiModelProperties;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static springdox.documentation.swagger.readers.parameter.ParameterAllowableReader.*;

@Component
public class SwaggerExpandedParameterBuilder implements ExpandedParameterBuilderPlugin {

  @Override
  public void apply(ParameterExpansionContext context) {
    Optional<ApiModelProperty> apiModelPropertyOptional = ApiModelProperties.findApiModePropertyAnnotation(context.getField());
    if (apiModelPropertyOptional.isPresent()) {
      fromApiModelProperty(context, apiModelPropertyOptional.get());
    }
    Optional<ApiParam> apiParamOptional = Annotations.findApiParamAnnotation(context.getField());
    if (apiParamOptional.isPresent()) {
      fromApiParam(context, apiParamOptional.get());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  private void fromApiParam(ParameterExpansionContext context, ApiParam apiParam) {
    String allowableProperty = emptyToNull(apiParam.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField());
    String name = isNullOrEmpty(context.getParentName())
            ? context.getField().getName()
            : String.format("%s.%s", context.getParentName(), context.getField().getName());
    context.getParameterBuilder()
            .name(name)
            .description(apiParam.value())
            .defaultValue(apiParam.defaultValue())
            .required(apiParam.required())
            .allowMultiple(apiParam.allowMultiple())
            .modelRef(new ModelRef(context.getDataTypeName()))
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(apiParam.access())
            .build();
  }

  private void fromApiModelProperty(ParameterExpansionContext context, ApiModelProperty apiModelProperty) {
    String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField());
    String name = isNullOrEmpty(context.getParentName()) ? context.getField().getName() : String.format("%s.%s",
            context.getParentName(),
            context.getField().getName());
    context.getParameterBuilder()
            .name(name)
            .description(apiModelProperty.value())
            .defaultValue(null)
            .required(apiModelProperty.required())
            .allowMultiple(Boolean.FALSE)
            .modelRef(new ModelRef(context.getDataTypeName()))
            .allowableValues(allowable).parameterType("query")
            .parameterAccess(apiModelProperty.access())
            .build();
  }

  private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
    } else if (optionalAllowable.isPresent()) {
      allowable = allowableValueFromString(optionalAllowable.get());
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
  }
}
