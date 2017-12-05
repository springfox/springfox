/*
 *
 *  Copyright 2016 the original author or authors.
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
package springfox.documentation;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

public class RequestHandlerKey {

  private final Set<String> pathMappings;
  private final Set<RequestMethod> supportedMethods;
  private final Set<? extends MediaType> supportedMediaTypes;
  private final Set<? extends MediaType> producibleMediaTypes;

  public RequestHandlerKey(
      Set<String> pathMappings,
      Set<RequestMethod> supportedMethods,
      Set<? extends MediaType> supportedMediaTypes,
      Set<? extends MediaType> producibleMediaTypes) {

    this.pathMappings = pathMappings;
    this.supportedMethods = supportedMethods;
    this.supportedMediaTypes = supportedMediaTypes;
    this.producibleMediaTypes = producibleMediaTypes;
  }

  public Set<String> getPathMappings() {
    return pathMappings;
  }

  public Set<RequestMethod> getSupportedMethods() {
    return supportedMethods;
  }

  public Set<? extends MediaType> getSupportedMediaTypes() {
    return supportedMediaTypes;
  }

  public Set<? extends MediaType> getProducibleMediaTypes() {
    return producibleMediaTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestHandlerKey that = (RequestHandlerKey) o;
    return Sets.symmetricDifference(pathMappings, that.pathMappings).isEmpty() &&
        Sets.symmetricDifference(supportedMethods, that.supportedMethods).isEmpty() &&
        Sets.symmetricDifference(supportedMediaTypes, that.supportedMediaTypes).isEmpty() &&
        Sets.symmetricDifference(producibleMediaTypes, that.producibleMediaTypes).isEmpty();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(pathMappings, supportedMethods, supportedMediaTypes, producibleMediaTypes);
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("RequestHandlerKey{");
    sb.append("pathMappings=").append(pathMappings);
    sb.append(", supportedMethods=").append(supportedMethods);
    sb.append(", supportedMediaTypes=").append(supportedMediaTypes);
    sb.append(", producibleMediaTypes=").append(producibleMediaTypes);
    sb.append('}');
    return sb.toString();
  }
}
