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

import java.util.Objects;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

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
  public int hashCode() {
    return Objects.hash(pathMappings, producibleMediaTypes, supportedMediaTypes, supportedMethods);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    RequestHandlerKey other = (RequestHandlerKey) obj;
    return Objects.equals(pathMappings, other.pathMappings)
        && Objects.equals(producibleMediaTypes, other.producibleMediaTypes)
        && Objects.equals(supportedMediaTypes, other.supportedMediaTypes)
        && Objects.equals(supportedMethods, other.supportedMethods);
  }

}
