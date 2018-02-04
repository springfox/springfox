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
import springfox.documentation.annotations.Incubating;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.*;

@Incubating("2.7.0")
public class AlternateTypePropertyBuilder {
  private Class<?> clazz;
  private String name;
  private boolean canRead;
  private boolean canWrite;
  private List<Annotation> annotations;

  public AlternateTypePropertyBuilder withType(Class<?> clazz) {
    this.clazz = clazz;
    return this;
  }

  public AlternateTypePropertyBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public AlternateTypePropertyBuilder withCanRead(boolean canRead) {
    this.canRead = canRead;
    return this;
  }

  public AlternateTypePropertyBuilder withCanWrite(boolean canWrite) {
    this.canWrite = canWrite;
    return this;
  }

  public AlternateTypePropertyBuilder withAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
    return this;
  }

  public DynamicType.Builder<Object> apply(DynamicType.Builder<Object> builder) {
    if (annotations == null) {
      annotations = new ArrayList<Annotation>();
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