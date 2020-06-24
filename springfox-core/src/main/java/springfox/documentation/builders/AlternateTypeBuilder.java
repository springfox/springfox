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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlternateTypeBuilder {
  private String fullyQualifiedClassName;
  private final List<AlternateTypePropertyBuilder> properties = new ArrayList<>();
  private final List<Annotation> annotations = new ArrayList<>();

  public AlternateTypeBuilder fullyQualifiedClassName(String fullyQualifiedClassName) {
    this.fullyQualifiedClassName = fullyQualifiedClassName;
    return this;
  }

  /**
   * @param property - properties for the alternate type
   * @return AlternateTypeBuilder
   * @see AlternateTypeBuilder#property(Consumer)  instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypeBuilder property(AlternateTypePropertyBuilder property) {
    this.properties.add(property);
    return this;
  }

  @SuppressWarnings("deprecation")
  //Will go away when constructor becomes package private
  public AlternateTypeBuilder property(@NonNull Consumer<AlternateTypePropertyBuilder> property) {
    AlternateTypePropertyBuilder builder = new AlternateTypePropertyBuilder();
    property.accept(builder);
    this.properties.add(builder);
    return this;
  }

  public AlternateTypeBuilder annotations(List<Annotation> annotations) {
    this.annotations.addAll(annotations);
    return this;
  }

  /**
   * @param properties - properties for the alternate type
   * @return AlternateTypeBuilder
   * @see AlternateTypeBuilder#properties instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypeBuilder withProperties(List<AlternateTypePropertyBuilder> properties) {
    this.properties.addAll(properties);
    return this;
  }

  /**
   * @param annotations - annotations
   * @return - AlternateTypeBuilder
   * @see AlternateTypeBuilder#annotations instead
   * @deprecated @since 3.0.0
   */
  @Deprecated
  public AlternateTypeBuilder withAnnotations(List<Annotation> annotations) {
    this.annotations.addAll(annotations);
    return this;
  }

  public Class<?> build() {
    DynamicType.Builder<Object> builder = new ByteBuddy()
        .subclass(Object.class)
        .name(fullyQualifiedClassName)
        .annotateType(annotations);
    for (AlternateTypePropertyBuilder each : properties) {
      builder = each.apply(builder);
    }
    return builder.make()
                  .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                  .getLoaded();
  }
}