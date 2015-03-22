package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mangofactory.swagger.models.dto.AllowableListValues;
import com.mangofactory.swagger.models.dto.AllowableRangeValues;
import com.mangofactory.swagger.models.dto.AllowableValues;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;
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
      List<String> singleVal = asList(allowableValueString.trim());
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

  public static Function<ApiModelProperty, Integer> toPosition() {
    return new Function<ApiModelProperty, Integer>() {
      @Override
      public Integer apply(ApiModelProperty annotation) {
        return annotation.position();
      }
    };
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
