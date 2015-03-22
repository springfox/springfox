package springdox.documentation.swagger.schema;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import springdox.documentation.service.AllowableListValues;
import springdox.documentation.service.AllowableRangeValues;
import springdox.documentation.service.AllowableValues;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.springframework.util.StringUtils.*;

public final class ApiModelProperties {

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  public static Function<ApiModelProperty, AllowableValues> toAllowableValues() {
    return new Function<ApiModelProperty, AllowableValues>() {
      @Override
      public AllowableValues apply(ApiModelProperty annotation) {
        return allowableValueFromString(annotation.allowableValues());
      }
    };
  }

  public static AllowableValues allowableValueFromString(String allowableValueString) {
    AllowableValues allowableValues = new AllowableListValues(Lists.<String>newArrayList(), "LIST");
    allowableValueString = allowableValueString.trim().replaceAll(" ", "");
    if (allowableValueString.startsWith("range[")) {
      allowableValueString = allowableValueString.replaceAll("range\\[", "").replaceAll("]", "");
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      List<String> ranges = newArrayList(split);
      allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
    } else if (allowableValueString.contains(",")) {
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      allowableValues = new AllowableListValues(newArrayList(split), "LIST");
    } else if (hasText(allowableValueString)) {
      List<String> singleVal = Collections.singletonList(allowableValueString.trim());
      allowableValues = new AllowableListValues(singleVal, "LIST");
    }
    return allowableValues;
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
