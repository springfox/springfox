/*
 *
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.bean.validators.plugins.models;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.*;

public class BeanValidatorsTestModel {

  private String noAnnotation;
  @NotNull
  private String annotationOnField;
  private String annotationOnGetter;
  @Mandatory
  private String compositeAnnotationOnField;
  private String compositeAnnotationOnGetter;
  @MandatoryInteger
  private String extraCompositeAnnotationOnField;
  private String extraCompositeAnnotationOnGetter;

  @Pattern(regexp = "overridden")
  @MandatoryInteger
  private String override;

  private String overrideOnGetter;

  public String getNoAnnotation() {
    return noAnnotation;
  }

  public void setNoAnnotation(String noAnnotation) {
    this.noAnnotation = noAnnotation;
  }

  public String getAnnotationOnField() {
    return annotationOnField;
  }

  public void setAnnotationOnField(String annotationOnField) {
    this.annotationOnField = annotationOnField;
  }

  @NotNull
  public String getAnnotationOnGetter() {
    return annotationOnGetter;
  }

  public void setAnnotationOnGetter(String annotationOnGetter) {
    this.annotationOnGetter = annotationOnGetter;
  }

  public String getCompositeAnnotationOnField() {
    return compositeAnnotationOnField;
  }

  public void setCompositeAnnotationOnField(String compositeAnnotationOnField) {
    this.compositeAnnotationOnField = compositeAnnotationOnField;
  }

  @Mandatory
  public String getCompositeAnnotationOnGetter() {
    return compositeAnnotationOnGetter;
  }

  public void setCompositeAnnotationOnGetter(String compositeAnnotationOnGetter) {
    this.compositeAnnotationOnGetter = compositeAnnotationOnGetter;
  }

  public String getExtraCompositeAnnotationOnField() {
    return extraCompositeAnnotationOnField;
  }

  public void setExtraCompositeAnnotationOnField(String extraCompositeAnnotationOnField) {
    this.extraCompositeAnnotationOnField = extraCompositeAnnotationOnField;
  }

  @MandatoryInteger
  public String getExtraCompositeAnnotationOnGetter() {
    return extraCompositeAnnotationOnGetter;
  }

  public void setExtraCompositeAnnotationOnGetter(String extraCompositeAnnotationOnGetter) {
    this.extraCompositeAnnotationOnGetter = extraCompositeAnnotationOnGetter;
  }

  public String getOverride() {
    return override;
  }

  public void setOverride(String override) {
    this.override = override;
  }

  @Pattern(regexp = "overriddenOnGetter")
  @MandatoryInteger
  public String getOverrideOnGetter() {
    return overrideOnGetter;
  }

  public void setOverrideOnGetter(String overrideOnGetter) {
    this.overrideOnGetter = overrideOnGetter;
  }

  /**
   * Composite validation constraint - @NotNull should still be detected!
   */
  @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
  @Retention(RUNTIME)
  @Constraint(validatedBy = {})
  @NotNull
  @Size(min = 1)
  @ReportAsSingleViolation
  public @interface Mandatory {

    String message() default "must be supplied!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

  /**
   * Extra composite validation constraint - @NotNull (from @Mandatory) should still be detected.
   */
  @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
  @Retention(RUNTIME)
  @Constraint(validatedBy = {})
  @Mandatory
  @Pattern(regexp = "[0-9]+")
  @ReportAsSingleViolation
  public @interface MandatoryInteger {

    String message() default "must be an integer";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
  }

}
