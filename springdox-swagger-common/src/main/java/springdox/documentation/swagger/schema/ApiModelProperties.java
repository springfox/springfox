package springdox.documentation.swagger.schema;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import springdox.documentation.service.AllowableListValues;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static com.google.common.base.Strings.*;

public final class ApiModelProperties {

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  public static Function<ApiModelProperty, AllowableListValues> toAllowableList() {
    return new Function<ApiModelProperty,
            AllowableListValues>() {
      @Override
      public AllowableListValues apply(ApiModelProperty annotation) {
        List<String> allowableValues
                = Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(nullToEmpty(annotation.allowableValues()));
        return new AllowableListValues(allowableValues, "LIST");
      }
    };
  }

  public static Function<ApiModelProperty, Boolean> toIsRequired() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.required();
      }
    };
  }

  public static Function<ApiModelProperty, String> toDescription() {
    return new Function<ApiModelProperty, String>() {
      @Override
      public String apply(ApiModelProperty annotation) {
        String description = "";
        if (!Strings.isNullOrEmpty(annotation.value())) {
          description = annotation.value();
        } else if (!Strings.isNullOrEmpty(annotation.notes())) {
          description = annotation.notes();
        }
        return description;
      }
    };
  }

  public static Optional<ApiModelProperty> findApiModePropertyAnnotation(AnnotatedElement annotated) {
    return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiModelProperty.class));
  }

  public static Function<ApiModelProperty, Boolean> toHidden() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.hidden();
      }
    };
  }
}
