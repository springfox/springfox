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

package springfox.documentation.spi;

import org.springframework.http.MediaType;
import org.springframework.plugin.metadata.SimplePluginMetadata;

public class DocumentationType extends SimplePluginMetadata {
  public static final DocumentationType SWAGGER_12 = new DocumentationType("swagger", "1.2");
  public static final DocumentationType SWAGGER_2 = new DocumentationType("swagger", "2.0");
  public static final DocumentationType OAS_30 = new DocumentationType("openApi", "3.0");
  public static final DocumentationType SPRING_WEB = new DocumentationType("spring-web", "1.0");
  private final MediaType mediaType;

  /**
   * Creates a new instance of {@code SimplePluginMetadata}.
   *
   * @param name      must not be {@literal null}.
   * @param version   must not be {@literal null}.
   * @param mediaType must not be {@literal null}
   */
  public DocumentationType(String name, String version, MediaType mediaType) {
    super(name, version);
    this.mediaType = mediaType;
  }

  public DocumentationType(String name, String version) {
    this(name, version, MediaType.APPLICATION_JSON);
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DocumentationType)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    DocumentationType that = (DocumentationType) o;

    return super.equals(that) && mediaType.equals(that.mediaType);

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + mediaType.hashCode();
    return result;
  }
}
