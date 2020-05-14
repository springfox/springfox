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
package springfox.documentation.service;

import java.util.List;
import java.util.Objects;

public class DocumentationReference {
  private String description = null;
  private String url = null;
  private List<VendorExtension> extensions = null;

  public DocumentationReference(
      String description,
      String url,
      List<VendorExtension> extensions) {

    this.description = description;
    this.url = url;
    this.extensions = extensions;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public List<VendorExtension> getExtensions() {
    return extensions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentationReference that = (DocumentationReference) o;
    return Objects.equals(
        description,
        that.description) &&
        Objects.equals(
            url,
            that.url) &&
        Objects.equals(
            extensions,
            that.extensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        description,
        url,
        extensions);
  }

  @Override
  public String toString() {
    return "DocumentationReference{" +
        "description='" + description + '\'' +
        ", url='" + url + '\'' +
        ", extensions=" + extensions +
        '}';
  }
}
