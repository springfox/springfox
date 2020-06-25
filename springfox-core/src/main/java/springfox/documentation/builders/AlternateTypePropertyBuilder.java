/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.builders;

import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.*;

public class AlternateTypePropertyBuilder {
  private Class<?> clazz;
  private String name;
  private boolean canRead;
  private boolean canWrite;
  private List<Annotation> annotations;


  /**
   * Should use available fluent builders
   *
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder() {
  }

  public AlternateTypePropertyBuilder type(Class<?> clazz) {
    this.clazz = clazz;
    return this;
  }

  public AlternateTypePropertyBuilder name(String name) {
    this.name = name;
    return this;
  }

  public AlternateTypePropertyBuilder canRead(boolean canRead) {
    this.canRead = canRead;
    return this;
  }

  public AlternateTypePropertyBuilder canWrite(boolean canWrite) {
    this.canWrite = canWrite;
    return this;
  }

  public AlternateTypePropertyBuilder annotations(List<Annotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  /**
   * @param clazz - name of the type
   * @return AlternateTypeBuilder
   * @see AlternateTypePropertyBuilder#type(Class)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder withType(Class<?> clazz) {
    this.clazz = clazz;
    return this;
  }

  /**
   * @param name - name of the type
   * @return AlternateTypeBuilder
   * @see AlternateTypePropertyBuilder#name(String)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @param canRead - properties for the alternate type
   * @return AlternateTypeBuilder
   * @see AlternateTypePropertyBuilder#canRead(boolean)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder withCanRead(boolean canRead) {
    this.canRead = canRead;
    return this;
  }

  /**
   * @param canWrite - properties for the alternate type
   * @return AlternateTypeBuilder
   * @see AlternateTypePropertyBuilder#canWrite(boolean)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder withCanWrite(boolean canWrite) {
    this.canWrite = canWrite;
    return this;
  }

  /**
   * @param annotations - properties for the alternate type
   * @return AlternateTypeBuilder
   * @see AlternateTypePropertyBuilder#annotations(List)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypePropertyBuilder withAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  public DynamicType.Builder<Object> apply(DynamicType.Builder<Object> builder) {
    if (annotations == null) {
      annotations = new ArrayList<>();
    }
    DynamicType.Builder<Object> augmented = builder.defineField(name, clazz, Visibility.PRIVATE)
        .annotateField(annotations);
    if (canRead) {
      augmented = getter(augmented);
    }
    if (canWrite) {
      augmented = setter(augmented);
    }
    return augmented;
  }

  private DynamicType.Builder<Object> setter(DynamicType.Builder<Object> builder) {
    return builder
        .defineMethod("set" + capitalize(name), Void.TYPE, Visibility.PUBLIC)
        .withParameters(clazz)
        .intercept(FieldAccessor.ofField(name));
  }

  private DynamicType.Builder<Object> getter(DynamicType.Builder<Object> builder) {
    return builder
        .defineMethod("get" + capitalize(name), clazz, Visibility.PUBLIC)
        .intercept(FieldAccessor.ofField(name));
  }
}