/*
 *
 *  Copyright 2017-2019 the original author or authors.
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

import com.google.common.base.Optional;

public class Example {
  private final Object value;
  private final String mediaType;

  public Example(Object value) {
    this.value = value;
    this.mediaType = null;
  }

  public Example(
      String mediaType,
      Object value) {
    this.mediaType = mediaType;
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public Optional<String> getMediaType() {
    return Optional.fromNullable(mediaType);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
