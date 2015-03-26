/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeWithConstructorProperty {
  @JsonIgnore
  private final Foobar foobar;

  @JsonIgnore
  private Long visibleForSerialize;

  @JsonProperty
  @JsonInclude
  public Long getVisibleForSerialize() {
    return visibleForSerialize;
  }

  @JsonIgnore
  public void setVisibleForSerialize(Long visibleForSerialize) {
    this.visibleForSerialize = visibleForSerialize;
  }

  @JsonProperty("foobar")
  public String getFoobarCode() {
    return foobar == null ? null : foobar.name();
  }

  public Foobar getFoobar() {
    return foobar;
  }

  public TypeWithConstructorProperty(@JsonProperty("foobar") String foobar) {
    this.foobar = Foobar.valueOf(foobar);
  }

  public enum Foobar {
    FOO,
    BAR
  }
}
