package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

import static com.google.common.base.Strings.*;

public final class ApiModelProperties {

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  public static Function<ApiModelProperty, List<String>> toAllowableList() {
    return new Function<ApiModelProperty, List<String>>() {
      @Override
      public List<String> apply(ApiModelProperty annotation) {
        return Splitter.on(',').omitEmptyStrings().splitToList(nullToEmpty(annotation.allowableValues()));
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
}
